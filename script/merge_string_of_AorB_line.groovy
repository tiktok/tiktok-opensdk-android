#!/usr/bin/env groovy
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.NodeList
import org.xml.sax.InputSource

import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerConfigurationException
import javax.xml.transform.TransformerException
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

/**
 * author: brook.wang@bytedance.com
 *
 * -----readme--------
 * 次脚本用于将各条业务线release分支的字符串合并到当前所在分支，需要合并的业务线分支请在【BRANCHES】数组中配置
 * 判断groovy环境：groovy -v
 * groovy环境安装：brew install groovy
 * 准备好环境后在工程中本脚本所在目录（也就是工程的根目录）下执行：groovy merge_string_of_AorB_line.groovy
 *
 * tips: 脚本切换分支然后依次pull可能会比较慢，建议自己本地先把需要的分支拉好
 *
 * */

//配置需要合并字符串的分支
//TODO 请在local.properties中配置需要的分支
//TODO 例如A线：branches=tools/release;aweme_main/release;release;social/release_live（不要有空格！！！）
Properties properties = new Properties()
InputStream inputStream = new FileInputStream('../local.properties')
properties.load( inputStream )
String[] BRANCHES = properties.getProperty( 'branches' ).split(';')
inputStream.close()

println "...Configed Branches... "
for (String branch : BRANCHES) {
    println branch
}

println "=========== start merge, @ @ ==========="
XmlHelper helper = new XmlHelper()
//创建临时目录，并将当前分支的字符串文件拷贝过去、
helper.createTargetFilesInTemDir()

Runtime rt = Runtime.getRuntime()

//获取当前分支名
Process p = rt.exec("git symbolic-ref --short -q HEAD")
InputStream fis = p.getInputStream()
InputStreamReader isr = new InputStreamReader(fis)
BufferedReader br = new BufferedReader(isr)
String currentBranch = br.readLine()
println "current in branch: " + currentBranch

String command = null
//切换所有目标分支，并将各自的字符串文件合并到临时目录下
for (String branch : BRANCHES) {
    command = String.format("git checkout %s", branch)
    println "check to branch: " + branch
    rt.exec(command).waitFor()

    println "git pull..."
    command = "git pull"
    rt.exec(command).waitFor()

    println "merge to temp files"
    helper.mergeBranchesToTempFiles()
}

//切换回原来的分支，将临时文件下的字符串合并到对应的文件
command = String.format("git checkout %s", currentBranch)
println "check to branch: " + currentBranch
rt.exec(command).waitFor()
println "merge temp strings to current branch"
helper.mergeTempFilesToCurrentBranch(helper.getTempDir())

CrowdinFileDownloader downloader = new CrowdinFileDownloader()
downloader.update_en_translate_file()
helper.mergeTempFilesToCurrentBranch(downloader.getTempDir())

downloader.clearTempDir()
helper.clearTempDir()

println "=========== end merge, pls check git status of your string files. ^ ^ ==========="
//打印多条分支都修改的字符串告警信息
helper.printConflictStrings()

class XmlHelper {
    Set<String> CONFLICT_STRINGS = new HashSet<>()
    Set<String> EXISTS_STRINGS = new HashSet<>()
    Set<String> EXISTS_ARRAYS = new HashSet<>()
    Set<String> EXISTS_STRING_ARRAYS = new HashSet<>()
    HashMap<String, String> SRC_STRING_FILES = new HashMap<>()
    DocumentBuilderFactory factory
    DocumentBuilder builder
    File tempDir

    XmlHelper() {
        SRC_STRING_FILES.put("strings.xml", "../app/src/i18n/res/values/strings.xml")
        SRC_STRING_FILES.put("mus-strings.xml", "../app/src/musically/res/values/mus-strings.xml")
        SRC_STRING_FILES.put("tiktok-strings.xml", "../app/src/tiktok/res/values/tiktok-strings.xml")

        factory = DocumentBuilderFactory.newInstance()
    }

    void createTargetFilesInTemDir() {
        tempDir = new File("aweme_string_temp")
        if (tempDir.exists()) {
            tempDir.delete()
        }
        tempDir.mkdir()
        for (String path : SRC_STRING_FILES.values()) {
            File tempFile = new File(tempDir, new File(path).getName())
            if (tempFile.exists()) {
                tempFile.delete()
            }
            copyFile(new File(path), tempFile)
            insertFakeChar(tempFile)
        }
    }

    void mergeBranchesToTempFiles() {
        for (File file : tempDir.listFiles()) {
            parseTargetXml(file.getAbsolutePath())
            insertFakeChar(new File(SRC_STRING_FILES.get(file.getName())))
            writeStringsToTarget(SRC_STRING_FILES.get(file.getName()), file.getAbsolutePath())
            clearCaches()
            removeFakeChar(new File(SRC_STRING_FILES.get(file.getName())))
        }
    }

    void mergeTempFilesToCurrentBranch(File tempParentDir) {
        for (String path : SRC_STRING_FILES.values()) {
            File tempFile = new File(tempParentDir, new File(path).getName())
            insertFakeChar(tempFile)
            insertFakeChar(new File(path))
            parseTargetXml(path)
            writeStringsToTarget(tempFile.getAbsolutePath(), path)
            clearCaches()
            removeFakeChar(new File(path))
        }
    }

    void printConflictStrings() {
        if (!CONFLICT_STRINGS.isEmpty()) {
            println "############# CONFLICT WARNING! check to retain one string from different values of keys bellow: "
            for (String key : CONFLICT_STRINGS) {
                println key
            }
            println "############# WARNING END!"
        }
    }
    void clearTempDir() {
        println "XmlHelper clear temp dir ..."
        if (tempDir.exists()) {
            Runtime.getRuntime().exec("rm -rf " + tempDir.getAbsolutePath()).waitFor()
        }
    }

    File getTempDir() {
        return tempDir
    }

    private void clearCaches() {
        EXISTS_ARRAYS.clear()
        EXISTS_STRINGS.clear()
        EXISTS_STRING_ARRAYS.clear()
    }

    private void parseTargetXml(String path) {
        Document document = getXmlDocument(path)
        if (document == null) {
            return
        }

        Element rootElement = document.getDocumentElement()
        NodeList stringList = rootElement.getElementsByTagName("string")
        for (int i = 0; i < stringList.getLength(); i++) {
            Element element = (Element) stringList.item(i)

            if (element.hasAttribute("name")) {
                EXISTS_STRINGS.add(element.getAttribute("name"))
            }
        }

        NodeList stringArrayList = rootElement.getElementsByTagName("string-array")
        for (int i = 0; i < stringArrayList.getLength(); i++) {
            Element element = (Element) stringArrayList.item(i)

            if (element.hasAttribute("name")) {
                EXISTS_STRING_ARRAYS.add(element.getAttribute("name"))
            }
        }

        NodeList arrayList = rootElement.getElementsByTagName("array")
        for (int i = 0; i < arrayList.getLength(); i++) {
            Element element = (Element) arrayList.item(i)

            if (element.hasAttribute("name")) {
                EXISTS_ARRAYS.add(element.getAttribute("name"))
            }
        }
    }

    private void writeStringsToTarget(String srcPath, String targetPath) {
        if (srcPath == null || targetPath == null) {
            return
        }
        Document srcDocument = getXmlDocument(srcPath)
        Document targetDocument = getXmlDocument(targetPath)

        if (srcDocument == null || targetDocument == null) {
            return
        }

        Element rootElement = srcDocument.getDocumentElement();
//        targetDocument.getDocumentElement().setAttribute("xmlns:ns1", rootElement.getAttribute("xmlns:ns1"));
//        targetDocument.getDocumentElement().setAttribute("xmlns:ns2", rootElement.getAttribute("xmlns:ns2"));
        NodeList stringList = rootElement.getElementsByTagName("string")
        for (int i = 0; i < stringList.getLength(); i++) {
            Element element = (Element) stringList.item(i)
            checkToAppendElement(targetDocument, element)
        }

        NodeList stringArrayList = rootElement.getElementsByTagName("string-array")
        for (int i = 0; i < stringArrayList.getLength(); i++) {
            Element element = (Element) stringArrayList.item(i)
            checkToAppendElement(targetDocument, element)
        }

        NodeList arrayList = rootElement.getElementsByTagName("array")
        for (int i = 0; i < arrayList.getLength(); i++) {
            Element element = (Element) arrayList.item(i)
            checkToAppendElement(targetDocument, element)
        }

        TransformerFactory transFactory = TransformerFactory.newInstance()
        try {
            //创建transformer对象
            Transformer transformer = transFactory.newTransformer()
            transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8")
            //设置换行
            transformer.setOutputProperty(OutputKeys.INDENT, "yes")
//            transformer.setOutputProperty(OutputKeys.METHOD, "xml")
            transformer.setOutputProperty(OutputKeys.STANDALONE, "yes")
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4")
            //构造转换,参数都是抽象类，要用的却是更具体的一些类，这些的类的命名有一些规律的。
            transformer.transform(new DOMSource(targetDocument), new StreamResult(targetPath))
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

    private Document getXmlDocument(String path) {
        InputStream is = null
        try {
            builder = factory.newDocumentBuilder()
            is = new FileInputStream(path)
            InputSource inputSource = new InputSource(is)
            inputSource.setEncoding("utf-8")
            Document result = builder.parse(inputSource)
            result.setXmlStandalone(true)
            is.close()
            return result
        } catch (Exception e) {
            e.printStackTrace()
            return null
        }
    }

    private void checkToAppendElement(Document document, Element element) {
        String name = element.getAttribute("name")
        Element rootElement = document.getDocumentElement()
        if (!EXISTS_STRING_ARRAYS.contains(name)
                && !EXISTS_STRINGS.contains(name)
                && !EXISTS_ARRAYS.contains(name)) {
            rootElement.appendChild(document.importNode(element, true))
        } else {
            if (EXISTS_STRINGS.contains(name)) {
                NodeList stringList = rootElement.getElementsByTagName("string")
                checkToWarnModifier(stringList, element)
            }
            if (EXISTS_ARRAYS.contains(name)) {
                NodeList stringList = rootElement.getElementsByTagName("array")
                checkToWarnModifier(stringList, element)
            }
            if (EXISTS_STRING_ARRAYS.contains(name)) {
                NodeList stringList = rootElement.getElementsByTagName("string-array")
                checkToWarnModifier(stringList, element)
            }
        }
    }

    private void checkToWarnModifier(NodeList stringList, Element element) {
        String name = element.getAttribute("name")
        Element elementTemp
        for (int i = 0; i < stringList.getLength(); i++) {
            elementTemp = (Element) stringList.item(i)
            if ((elementTemp.getAttribute("name") == name) && (elementTemp.getTextContent().trim() != element.getTextContent().trim())) {
                CONFLICT_STRINGS.add(name)
            }
        }
    }

    void copyFile(File sourceFile, File targetFile) throws IOException {
        FileInputStream input = new FileInputStream(sourceFile)
        InputStreamReader inBuff = new InputStreamReader(input)

        FileOutputStream output = new FileOutputStream(targetFile)
        OutputStreamWriter ow = new OutputStreamWriter(output)

        char[] buffer = new char[1024 * 4]
        int len
        while ((len = inBuff.read(buffer)) != -1) {
            ow.write(buffer, 0, len)
        }
        ow.flush()

        //关闭流
        inBuff.close()
        ow.close()
        output.close()
        input.close()
    }

    /**
     *
     * 为了解决&#160;这样的转义字符在xml解析时直接会被转义，这里插入一些破坏字符
     * 下一个方法是为了去除破坏字符
     * */
    void insertFakeChar(File file) {
        FileInputStream input = new FileInputStream(file)
        InputStreamReader inBuff = new InputStreamReader(input)

        File outPutFile = new File(file.getParent(), file.getName() + "_temp")
        FileOutputStream output = new FileOutputStream(outPutFile)
        OutputStreamWriter ow = new OutputStreamWriter(output)

        // 缓冲数组
        char[] buffer = new char[1024 * 2]
        char[] tempBuffer = new char[1024 * 3]
        int len
        int tempLen
        int tempIndex = 0
        while ((len = inBuff.read(buffer)) != -1) {
            tempLen = len
            tempIndex = 0
            for (int i = 0; i < len; i++) {
                if ("&".equals(buffer[i].toString())) {
                    tempBuffer[tempIndex] = '*'
                    tempIndex++
                    tempBuffer[tempIndex] = '~'
                    tempIndex++
                    tempLen += 1
                } else {
                    tempBuffer[tempIndex] = buffer[i]
                    tempIndex += 1
                }
            }
            ow.write(tempBuffer, 0, tempLen)
        }
        // 刷新此缓冲的输出流
        ow.flush()

        //关闭流
        inBuff.close()
        ow.close()
        output.close()
        input.close()
        outPutFile.renameTo(file.getAbsolutePath())
    }

    void removeFakeChar(File file) {
        FileInputStream input = new FileInputStream(file)
        InputStreamReader inBuff = new InputStreamReader(input)

        File outPutFile = new File(file.getParent(), file.getName() + "_temp")
        FileOutputStream output = new FileOutputStream(outPutFile)
        OutputStreamWriter ow = new OutputStreamWriter(output)

        // 缓冲数组
        char[] buffer = new char[1024 * 2]
        char[] tempBuffer = new char[1024 * 2]
        int len
        int tempLen
        int tempIndex = 0
        while ((len = inBuff.read(buffer)) != -1) {
            tempLen = len
            tempIndex = 0
            for (int i = 0; i < len; i++) {
                if ("*".equals(buffer[i].toString())
                        && (i+1 < len) && "~".equals(buffer[i + 1].toString())) {
                    tempBuffer[tempIndex] = '&'
                    tempIndex += 1
                    i += 1
                    tempLen -= 1
                } else {
                    tempBuffer[tempIndex] = buffer[i]
                    tempIndex += 1
                }
            }
            ow.write(tempBuffer, 0, tempLen)
        }
        // 刷新此缓冲的输出流
        ow.flush()

        //关闭流
        inBuff.close()
        ow.close()
        output.close()
        input.close()
        outPutFile.renameTo(file.getAbsolutePath())
    }
}

class CrowdinFileDownloader {
    String ZH_EN_CROWDING_KEY = "c48012205e14c47968ea24ea977ba042"
    String ZH_EN_PROJECT_NAME = "musically-tiktok-zh-en"
    String TRANSLATION_TMP = 'translation_tmp'

    void update_en_translate_file() {
        clearTempDir()
        mkTempDir()
        build_current_version(ZH_EN_PROJECT_NAME, ZH_EN_CROWDING_KEY)
        download_translation(ZH_EN_PROJECT_NAME, ZH_EN_CROWDING_KEY)
        deal_download_file(ZH_EN_PROJECT_NAME)
    }

    def clearTempDir() {
        println "CrowdinFileDownloader clear temp dir ..."
        if (new File(TRANSLATION_TMP).exists()) {
            Runtime.getRuntime().exec("rm -rf " + TRANSLATION_TMP).waitFor()
        }
    }


    def mkTempDir() {
        if (!new File(TRANSLATION_TMP).exists()) {
            Runtime.getRuntime().exec("mkdir " + TRANSLATION_TMP).waitFor()
            Runtime.getRuntime().exec("mkdir " + TRANSLATION_TMP + "/" + ZH_EN_PROJECT_NAME).waitFor()
        }
    }

    def build_current_version(project_name, key) {
        println "building..."
        Runtime.getRuntime().exec(
                "curl -s https://api.crowdin.com/api/project/" + project_name + "/export?key=" + key).waitFor()
    }


    def download_translation(project_name, key) {
        println "downloading..."
        Runtime.getRuntime().exec(
                "curl -s -o ./" + TRANSLATION_TMP + "/" + project_name + "/all.zip https://api.crowdin.com/api/project/"
                        + project_name + "/download/all.zip?key=" + key).waitFor()
    }


    def deal_download_file(project_name) {
        Runtime.getRuntime().exec("unzip ./" + TRANSLATION_TMP + "/" + project_name + "/all.zip" + " -d "
                + "./" + TRANSLATION_TMP + "/" + project_name).waitFor()
    }

    File getTempDir() {
        return new File(TRANSLATION_TMP + "/" + ZH_EN_PROJECT_NAME + "/en/Android/")
    }

}
