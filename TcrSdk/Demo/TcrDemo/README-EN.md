# Cloud Rendering Android demo project

## Directory introduction

Three demos can be packaged under app/src, namely test demo (tcrDemo), scene demo (scenes) and the lightest simple demo (simple).
Package different demo apks through build variants.
There are two types of build methods for test demo, full and lite. The difference is that full uses the full version of SDK and lite uses the lightweight version of SDK. The lite version needs to download and load the plug-in at runtime.
All three demos require you to generate an experience code through the Tencent Cloud console to experience it normally.
The test demo can enter the experience code in the main interface after running to experience it. The simple demo and scene demo need to fill in the experience code in the variable EXPERIENCE_CODE in the CloudRenderBiz.java file.

### 1. Demo introduction
### 1.1 simple demo
The simplest access demo only includes necessary object creation, connection creation and interaction establishment.
You can first refer to this demo to complete the most basic cloud rendering scene creation

### 1.2 Scene demo
Contains demo project demonstrations of several types of scenes. Currently, there are two scenes of custom rendering and custom decoding. You can view CustomRenderActivity and
MediaCodecActivity under scenes to learn how to use them.

### 1.3 Test demo
Basically covers all demo projects for sdk interface test calls, among which ExperiencePage is the entry point. Enter the experience code obtained in the Tencent Cloud console to connect and experience.

GamePlayFragment is the main entry point for connection, and TestApiHandler is the collection category for interface calls. Please read the code for details.
The UI named test_api under res/layout is the UI for API calls, which is classified as the use under the scene. You can view the integration of the interface in the project as needed.

### 2. Demo required parameters
There are two ways to start the demo, one is to start directly through the experience code, and the other is to deploy the business background to request session start.
#### 2.1 Experience code startup parameters
Set USE_TCR_TEST_ENV to true in app/src/main/simple(scenes)/CloudRenderBiz.java, and assign the application experience code obtained from Tencent Cloud Console-Cloud Rendering to EXPERIENCE_CODE.
Then compile and run the demo to experience cloud rendering
#### 2.2 Self-deployment service parameters
After [activating the cloud rendering service](https://cloud.tencent.com/document/product/1547/72707) and purchasing a concurrent binding project, you can deploy the cloud rendering business backend by yourself. [demo example](https://github.com/tencentyun/car-server-demo#3-%E5%90%AF%E5%8A%A8%E6%9C%8D%E5%8A%A1)

After binding the project, you will get an application id, and get the address after deploying the backend.
Then set USE_TCR_TEST_ENV to false in app/src/main/simple(scenes)/CloudRenderBiz.java, and fill in the parameters obtained above into PROJECT_ID and SERVER_URL respectively.
Then compile and run the demo to experience cloud rendering