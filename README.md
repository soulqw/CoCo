# CoCo
 [![Hex.pm](https://img.shields.io/badge/download-1.1.2-green)](https://www.apache.org/licenses/LICENSE-2.0)
 [![Hex.pm](https://img.shields.io/badge/Api-4.0%2B-yellow)]()
 [![Hex.pm](https://img.shields.io/hexpm/l/plug.svg)]()
 [![Hex.pm](https://img.shields.io/badge/Jetpack-AndroidX-red)]()
#### 一款小而美的的Android系统相机拍照、系统相册选择、裁剪库🐵
 - 一行代码完成从系统相机拍照、系统相册选择图片、系统裁剪
 - 内部适配 7.0 FileProvider文件处理，无需自己额外处理
 - 默认图片处理器自带两种图片压缩策略，并可按需自定义图片处理器
 - 支持Activity、Fragment,图片异步处理自动绑定相关容器生命周期
 - 全新设计的APi-更灵活-更易于理解
 - 完全基于Kotlin编写，与Java兼容
 - 全面适配AndroidX、配置简单，导入方便
## Installation：

 ![image](https://img-blog.csdnimg.cn/20191009181659912.png)

```java
dependencies {
    implementation 'com.qw:coco:1.1.2'
}
```
###### 最新Release 改动：
- Pick 选图在 Range.PICK_CONTENT 模式下可以指定过滤文件类型，如PNG、JPG、GIF等，默认所有格式
- CoCo 默认回调增加抽象类实现 CoCoAdapter

 CoCo 1.0.0 + 将迎来历史上最大的更新：

 强烈建议您迁移到最新的APi，方便后续新功能的拓展，老版本最后Release 版本将维护至[0.3.1](https://github.com/soulqw/CoCo/blob/developer/README_OLD.md)，后续不再更新(分支master_1.0.0_below)。

## Usage：
#### 基本用法

- 调用系统相机拍照
```kotlin
       CoCo.with(this@MainActivity)
                       .take(createSDCardFile())
                       .start(object : CoCoAdapter<TakeResult>() {

                           override fun onSuccess(data: TakeResult) {
                               iv_image.setImageBitmap(Utils.getBitmapFromFile(data.savedFile!!.absolutePath))
                           }
                       })
```
效果图：

![image](https://cdn.nlark.com/yuque/0/2020/gif/1502571/1601093298091-b091b479-05d0-435e-a650-ba5e07850d72.gif)

- 调用系统相册选择图片：

```kotlin
        CoCo.with(this@MainActivity)
                    .pick()
                    .start(object : CoCoAdapter<PickResult>() {
                        override fun onSuccess(data: PickResult) {

                        iv_image.setImageURI(data.originUri)

                        }
                    })
```
##### 效果图:

![image](https://cdn.nlark.com/yuque/0/2020/gif/1502571/1601093668141-533ce509-9f4e-45fa-99c7-57a9a3d31335.gif)

- 处理我们拿到的原图：

上述以上是原图的情形，通常情况下，我们常常要对原图做一些处理，比如压缩等，所以CoCo 提供了dispose操作符，方便获得图片之后做一些处理：
```kotlin
        //选择图片后压缩
         CoCo.with(this)
                .pick()
                //切换操作符
                .then()
                .dispose()
                .start(object : CoCoAdapter<DisposeResult>() {
                    override fun onSuccess(data: DisposeResult) {
                        iv_image.setImageBitmap(data.compressBitmap)
                    }
                })

```
我们通过 then 操作符来完成操作符的组合，可以进行一些列操作符的串联流式处理。

##### dispose 操作符：

dispose操作符可以自动在子线程处理我们要处理的文件，并且自动绑定with()容器中的生命周期

###### 它不仅可以和其它操作符组合使用：
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
###### 它还可以单独使用：
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
###### 系统默认Default 图片处理器可以帮我们完成图片处理，也可自定义处理逻辑：

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
#### Crop操作符：
让我可以指定一个图片文件提供给系统裁剪处理：

```kotlin
    CoCo.with(this@CropActivity)
                .crop(imageFile)
                .start(object : CoCoAdapter<CropResult>() {

                    override fun onSuccess(data: CropResult) {
                        iv_image.setImageBitmap(data.cropBitmap)
                    }

                })
```
当然，也可以组合原有操作符一起使用：

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
效果图：

![image](https://upload-images.jianshu.io/upload_images/11595074-7fba783db175f9ed.gif?imageMogr2/auto-orient/strip)

- 其它功能：
###### 每个操作符都可以添加回调监听：

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
- 半流程图：
![image](https://img-blog.csdnimg.cn/20201205192149134.png)

更多功能可参考Demo

### 截图：
![image](https://cdn.nlark.com/yuque/0/2020/png/1502571/1601094243032-2d14deb1-e487-4d6e-906e-fafe6845c654.png)

#### [原理和详细设计分享可参考这里](https://juejin.cn/post/6907620425837051917)

##### 联系我（注明来意）：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210209233837429.png)
##### 交流QQ群：714178759
