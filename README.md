# TikTok Developer SDK
## Capabilities
1. Share

## Branch Management
> Use the `develop` branch for development and testing
> Merge to the `master` branch after a release

## Publish

Please follow the instructions [here](https://bytedance.feishu.cn/docs/doccnIVScHWfdpGNaayhv2gG7vd#).
To publish internal (for Bytedance partners) and external (external partners) aar, follow this [link](https://bytedance.feishu.cn/docs/doccnabOaizRNv1cbXJ74qWoE8c)

## Response Handling
`IApiEventHandler` is used to handle the response from TiKTok, i.e. `onResp` and `onErrorIntent`. However, current implementation casts a constraint on the 3rd party app to redirect the response intent to the SDK for data handling (`IDataHandler`) before passing the event back to the 3rd party app via `IApiEventHandler` again. This intermediate call of handleIntent from the 3rd party app is redundant and sometimes let them create two instances of `TikTokOpenApi`, i.e. once from MainActivity to send the request and a second time in the `TikTokEntryActivity` to `handleIntent`. So, here we have an alternative approach to avoid this redundant middle-step.
Here let's review the current workflow:
* auth service:
1. [3rd party app] create TikTokOpenApi, optionally with `IApiEventHandler`
2. [3rd party app] call authenticate or share methods on TikTokOpenApi 
3. [3rd party app] in a custom local entry activity or a default `tiktok.TikTokEntryActivity`, create a second `TikTokOpenApi` and call `handleIntent` method on it and provide another `IApiEventHandler`
4. [SDK] the 2nd `TikTokOpenApi` gets the intent and let the corresponding `IDataHandler` parse the intent and compose a response object and call `onResp` method on the provided `IApiEventHandler` to complete the flow

Note that, 
1. `onReq` is not currently called on the `IApiEventHandler`. It's only used in webAuth where WebAuthActivity conforms to IApiEventHandler to get the request and then hold on to the web auth request and set its redirect URL. 
2. the first IApiEventHandler created in the MainActivity was never used, to handle the response.
3. step 3 above is redundant.

Alternative proposed the approach is to have an `TikTokApiReesponseActivity` in the SDK to handle the response intent from TikTok to replace step 3.
In this case, the 3rd party app no longer need to 1. create two `TikTokOpenApi` instances or create a custom entry activity or `tiktok.TikTokEntryActivity` or 3. forced to call `handleIntent` on the 2nd `TikTokOpenApi` instance. It's all handled by `TikTokApiReesponseActivity`
3rd party app now only needs to provide one `IApiEventHandler`, during the creation of the first and only one `IApiEventHandler`. Now, the workflow is the following: 
1. [3rd party app] create TikTokOpenApi with IApiEventHandler
2. [3rd party app] call authenticate or share request
3. [SDK] `TikTokApiReesponseActivity` handles the response intent and call a reused TikTokOpenApi to handle intent
4. [SDK] call the `onResp` method on the provided IApiEventHandler in step 1. 

To turn on this approach, set the switch flag `kRefactorResponseHandling` to true in both share service and auth service.
Note: `kRefactorResponseHandling` = true  does not with external SDK yet. Need to align with more RDs to adapt this approach. 