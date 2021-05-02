# CoCo
 [![Hex.pm](https://img.shields.io/badge/download-1.1.2-green)](https://www.apache.org/licenses/LICENSE-2.0)
 [![Hex.pm](https://img.shields.io/badge/Api-4.0%2B-yellow)]()
 [![Hex.pm](https://img.shields.io/hexpm/l/plug.svg)]()
 [![Hex.pm](https://img.shields.io/badge/Jetpack-AndroidX-red)]()
### [中文版说明](https://github.com/soulqw/CoCo/blob/master/README_CN.md)
#### An delicate lib for System Capture, Pick and Crop in Android 🐵
 - Provided an function the system camera to take a picture, system photo album selection, system cropping just need one code
 - The inner adjust the FileProvider in Android 7.0 above
 - The default image disposer provided two strategy to compress the image, it can also use custom disposer
 - Both Activity and Fragment supported，the asynchronous image dispose will bind their lifecycle automatic
 - The new designed api, easy to understand
 - Fully based on kotlin, also can worked with java
 - Already migrated to AndroidX, simplify to use
## Installation：

 ![image](https://img-blog.csdnimg.cn/20191009181659912.png)

```java
dependencies {
    implementation 'com.qw:coco:1.1.2'
}
```
###### The new in Release ：
- The Pick function in the Range.PICK_CONTENT model can filter the specific file type such as PNG、JPG、GIF and so on, the default config is no filter
- Add the default abstract class CoCoAdapter

## Usage：
#### basic usage

- Capture image in system camera
```kotlin
       CoCo.with(this@MainActivity)
                       .take(createSDCardFile())
                       .start(object : CoCoAdapter<TakeResult>() {

                           override fun onSuccess(data: TakeResult) {
                               iv_image.setImageBitmap(Utils.getBitmapFromFile(data.savedFile!!.absolutePath))
                           }
                       })
```
image sample：

![image](https://cdn.nlark.com/yuque/0/2020/gif/1502571/1601093298091-b091b479-05d0-435e-a650-ba5e07850d72.gif)

- Pic image in system Gallery：

```kotlin
        CoCo.with(this@MainActivity)
                    .pick()
                    .start(object : CoCoAdapter<PickResult>() {
                        override fun onSuccess(data: PickResult) {

                        iv_image.setImageURI(data.originUri)

                        }
                    })
```
##### image sample:

![image](https://cdn.nlark.com/yuque/0/2020/gif/1502571/1601093668141-533ce509-9f4e-45fa-99c7-57a9a3d31335.gif)

- dispose the origin image：
Generally speaking, we may need dispose the origin image such as compress and so on, so CoCo provide the operator of dispose, we can use it to dispose the image
```kotlin
        //pick then dispose
         CoCo.with(this)
                .pick()
                //switch the operator
                .then()
                .dispose()
                .start(object : CoCoAdapter<DisposeResult>() {
                    override fun onSuccess(data: DisposeResult) {
                        iv_image.setImageBitmap(data.compressBitmap)
                    }
                })

```
We use the then method to switch the operator, it can combination the another operators

##### dispose operator：

dispose operator can dispose the file in background thread automatic, it can also bind the related container,s lifecycle in method "with()"

###### It not only can works with other operators：
```kotlin
 CoCo.with(this)
                .take(createSDCardFile())
                .then()
                .dispose()
                .start(object : CoCoAdapter<DisposeResult>() {

                    override fun onSuccess(data: DisposeResult) {
                        iv_image.setImageBitmap(Utils.getBitmapFromFile(data.savedFile!!.absolutePath))
                    }
                })
```
###### It also can works separately：
```kotlin
        CoCo.with(this)
                .dispose()
                .origin(imageFile.path)
                .start(object : CoCoAdapter<DisposeResult>() {

                    override fun onSuccess(data: DisposeResult) {
                        iv_image.setImageBitmap(data.compressBitmap)
                    }
                })
```
###### We can use customDisposer to dispose the image,we can also custom the disposer：

```kotlin
              CoCo.with(this)
                .dispose()
                .disposer(CustomDisposer())
              //.disposer(DefaultImageDisposer())
                .origin(imageFile.path)
                .start(object : CoCoAdapter<DisposeResult>() {

                    override fun onSuccess(data: DisposeResult) {
                        iv_image.setImageBitmap(data.compressBitmap)
                    }
                })

                            /**
             * custom disposer
             * rotation image
             */
            class CustomDisposer : Disposer {
                override fun disposeFile(originPath: String, targetToSaveResult: File?): DisposeResult {
                    return DisposeResult().also {
                        var bitmap = QualityCompressor()
                            .compress(originPath, 80)
                        val m = Matrix()
                        m.postRotate(90f)
                        bitmap = Bitmap.createBitmap(
                            bitmap!!, 0, 0, bitmap.width,
                            bitmap.height, m, true
                        )
                        it.savedFile = targetToSaveResult
                        it.compressBitmap = bitmap
                    }
                }
            }

```
#### The Crop operator：
We can provide an image to system Crop:

```kotlin
    CoCo.with(this@CropActivity)
                .crop(imageFile)
                .start(object : CoCoAdapter<CropResult>() {

                    override fun onSuccess(data: CropResult) {
                        iv_image.setImageBitmap(data.cropBitmap)
                    }

                })
```
Of course, it can works with other combinations：

```kotlin
  CoCo.with(this@MainActivity)
                    .pick()
                    .then()
                    .crop()
                    .start(object : CoCoAdapter<CropResult>() {

                        override fun onSuccess(data: CropResult) {
                            iv_image.setImageBitmap(data.cropBitmap)
                        }
                    })
```
image sample：

![image](https://upload-images.jianshu.io/upload_images/11595074-7fba783db175f9ed.gif?imageMogr2/auto-orient/strip)

- Another functions：
###### every operator can add the call backs：

```kotlin
  CoCo.with(this@PickPictureActivity)
                .pick()
                .range(Range.PICK_CONTENT)
//                .range(Range.PICK_DICM)
                .callBack(object : PickCallBack {

                    override fun onFinish(result: PickResult) {
                        Log.d(MainActivity.TAG, "pick onFinish${result}")
                    }

                    override fun onCancel() {
                        Log.d(MainActivity.TAG, "pick onCancel")
                    }

                    override fun onStart() {
                        Log.d(MainActivity.TAG, "pick onStart")
                    }

                }).start(object : CoCoCallBack<PickResult> {

                    override fun onSuccess(data: PickResult) {
                        iv_image.setImageURI(data.originUri)
                    }

                    override fun onFailed(exception: Exception) {}
                })
```
- the flow image：
![image](https://img-blog.csdnimg.cn/20201205192149134.png)

More detail can use the demo

### The screenshot：
![image](https://cdn.nlark.com/yuque/0/2020/png/1502571/1601094243032-2d14deb1-e487-4d6e-906e-fafe6845c654.png)

#### [The blog for this lib](https://juejin.cn/post/6907620425837051917)

##### Contact me（tell me why）：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210209234455912.png)

