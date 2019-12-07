# CoCo
 [![Hex.pm](https://img.shields.io/badge/download-0.1.0-green)](https://www.apache.org/licenses/LICENSE-2.0)
 [![Hex.pm](https://img.shields.io/badge/Api-4.0%2B-yellow)]()
 [![Hex.pm](https://img.shields.io/hexpm/l/plug.svg)]()
 [![Hex.pm](https://img.shields.io/badge/Jetpack-AndroidX-red)]()
#### 一个更好用的Android系统相机拍照和系统相册选择库：
 - 一行代码完成从系统相机拍照或者系统相册选择图片
 - 内部适配 7.0 FileProvider文件处理，无需自己额外处理
 - 支持多种图片压缩策略，并可自定义压缩程度
 - 完全基于Kotlin编写，与Java兼容
 - 支持Activity、Fragment,图片压缩异步处理自动绑定相关生命周期
 - 配置简单，导入方便
 - 支持debug模式
## Installation：

 ![image](https://img-blog.csdnimg.cn/20191009181659912.png)

如果你的项目已经支持Android X:
```java
dependencies {
    implementation 'com.qw:coco:0.1.0_x'
}

```
如果你的项目还没迁移到Android X:
```java
dependencies {
    implementation 'com.qw:coco:0.1.0'
}

```
## Usage：
#### 调用系统相机拍照

- 默认原图的情形
```kotlin
  CoCo.with(this)
        .take(createSDCardFile())
        .apply()
        .start(object : GetImageCallBack<CaptureResult> {
                override fun onSuccess(data: CaptureResult) {
                    val bitmap: Bitmap = BitmapFactory.decodeFile(data.targetFile!!.path)
                    getImageView().setImageBitmap(bitmap)
                }

                override fun onCancel() {
                    Toast.makeText(this@TakePictureActivity, "拍照取消", Toast.LENGTH_SHORT).show()
                }

                override fun onFailed(exception: Exception) {
                    Toast.makeText(this@TakePictureActivity, "拍照异常: $exception", Toast.LENGTH_SHORT).show()
                }
            })
```
- 如果你需要拍完照对图片做一些处理（applyWithDispose 目前支持压缩，可以自定义压缩策略）：

```kotlin
  CoCo.with(this@TakePictureActivity)
                .take(createSDCardFile())
                .applyWithDispose()
                .start(object : GetImageCallBack<CaptureResult> {

                    override fun onDisposeStart() {
                        Toast.makeText(this@TakePictureActivity, "拍照成功,开始处理", Toast.LENGTH_SHORT).show()
                    }

                    override fun onCancel() {
                        Toast.makeText(this@TakePictureActivity, "拍照取消", Toast.LENGTH_SHORT).show()
                    }

                    override fun onFailed(exception: Exception) {
                        Toast.makeText(this@TakePictureActivity, "拍照异常: $exception", Toast.LENGTH_SHORT).show()
                    }

                    override fun onSuccess(data: CaptureResult) {
                        Toast.makeText(this@TakePictureActivity, "拍照操作最终成功", Toast.LENGTH_SHORT).show()
                        getImageView().setImageBitmap(data.compressBitmap)
                    }

                })
```
##### 效果图:
![image](https://upload-images.jianshu.io/upload_images/4346197-95a4098e9d4b7e98.gif)

- 也可以传入一个ImageDisposer来处理图片：

```kotlin
                CoCo.with(this)
                    .take(createSDCardFile())
                    .applyWithDispose(ImageDisposer().degree(15).strategy(CompressStrategy.MATRIX))
                    .start(object : GetImageCallBack<TakeResult> {

                        override fun onDisposeStart() {
                            Toast.makeText(this@MainActivity, "拍照成功,开始处理", Toast.LENGTH_SHORT).show()
                        }

                        override fun onCancel() {
                            Toast.makeText(this@MainActivity, "拍照取消", Toast.LENGTH_SHORT).show()
                        }

                        override fun onFailed(exception: Exception) {
                            Toast.makeText(this@MainActivity, "拍照异常: $exception", Toast.LENGTH_SHORT).show()
                        }

                        override fun onSuccess(data: TakeResult) {
                            Toast.makeText(this@MainActivity, "拍照操作最终成功", Toast.LENGTH_SHORT).show()
                            iv_image.setImageBitmap(data.compressBitmap)
                        }

                    })
```

#### 系统相册选择
APi与拍照相似，同样支持压缩，选择照片结果中提供原始Uri
- 仅仅是从系统相册选一张图片：

```kotlin
 CoCo.with(this)
    .pick()
    .apply()
    .start(object :GetImageCallBack<PickResult>{

          override fun onSuccess(data: PickResult) {
              Toast.makeText(
                  this@MainActivity,
                    "选择操作最终成功 path: ${data.originUri.path}",
                          Toast.LENGTH_SHORT
                            ).show()
           }

         override fun onFailed(exception: Exception) {
                Toast.makeText(this@MainActivity, "选择异常: $exception", Toast.LENGTH_SHORT).show()
           }
     })
```
- 如果需要跟拍照一样,选完后对图片做处理：
```kotlin
 CoCo.with(this)
    .pick(createSDCardFile())
    .applyWithDispose()
    .start(object : GetImageCallBack<PickResult> {

        override fun onDisposeStart() {
                        Toast.makeText(this@PickPictureActivity, "选择成功,开始处理", Toast.LENGTH_SHORT)
                            .show()
                    }

        override fun onSuccess(data: PickResult) {
                        Toast.makeText(
                            this@PickPictureActivity,
                            "选择操作最终成功 path: ${data.originUri.path}",
                            Toast.LENGTH_SHORT
                        ).show()
                        getImageView().setImageBitmap(data.compressBitmap)
                        tv_result.text = getImageSizeDesc(data.compressBitmap!!)
                    }


        override fun onFailed(exception: Exception) {
                    }
                })
```
### 截图：
![image](https://upload-images.jianshu.io/upload_images/4346197-45eef4367cc55ca1.png)

![image](https://upload-images.jianshu.io/upload_images/4346197-c5b04e7acad92ff3.png)

[GitHub地址](https://github.com/soulqw/SoulPhotoTaker/)