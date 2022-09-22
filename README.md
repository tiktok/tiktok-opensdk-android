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
`IApiEventHandler` is used to handle the response from TiKTok, i.e. `onResponse` and `onErrorIntent`.
1. [3rd party app] create TikTokOpenApi, optionally with `IApiEventHandler`
2. [3rd party app] call authenticate or share methods on TikTokOpenApi 
3. [3rd party app] in a custom local entry activity or a default `tiktok.TikTokEntryActivity`, create a second `TikTokOpenApi` and call `handleIntent` method on it and provide another `IApiEventHandler`
4. [SDK] the 2nd `TikTokOpenApi` gets the intent and compose a response object and call `onResp` method on the provided `IApiEventHandler` to complete the flow

Note that,
The first IApiEventHandler created in the MainActivity was never used, to handle the response.