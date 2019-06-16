# CoCo
 [![Hex.pm](https://img.shields.io/hexpm/l/plug.svg)](https://www.apache.org/licenses/LICENSE-2.0)
#### 一个更好用的Android系统相机拍照和系统相册选择库：
 -  一行代码完成从系统相机拍照或者系统相册选择图片
 - 支持多种策略的图片压缩，并可自定义压缩范围
 - 配置简单，导入方便
 - 完全基于Kotlin编写，与Java兼容
 - 支持debug模式
## Installation：

```java
dependencies {
    implementation 'com.qw:coco:0.0.5'
}

```
## Usage：
#### 拍照

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
- 也可以拍照完成以后压缩处理

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
#### 系统相册选择
APi与拍照相似，同样支持压缩，选择照片结果中提供原始Uri

```kotlin
       CoCo.with(this@MainActivity)
                    .pick(createSDCardFile())
                    .apply()
//                .applyWithDispose()
                    .start(object : GetImageCallBack<PickResult> {

                        override fun onDisposeStart() {
                            Toast.makeText(this@MainActivity, "选择成功,开始处理", Toast.LENGTH_SHORT).show()
                        }

                        override fun onSuccess(data: PickResult) {
                            Toast.makeText(
                                this@MainActivity,
                                "选择操作最终成功 path: ${data.originUri.path}",
                                Toast.LENGTH_SHORT
                            ).show()
                            val selectedPath = Utils.uriToImagePath(this@MainActivity, data.originUri)
                            val bitmap: Bitmap = BitmapFactory.decodeFile(selectedPath)
                            iv_image.setImageBitmap(bitmap)
//                         if you use applyWithDispose() can get compress bitmap
//                        iv_image.setImageBitmap(data.compressBitmap)

                        }

                        override fun onFailed(exception: Exception) {
                            Toast.makeText(this@MainActivity, "选择异常: $exception", Toast.LENGTH_SHORT).show()
                        }

                        override fun onCancel() {
                            Toast.makeText(this@MainActivity, "选择取消", Toast.LENGTH_SHORT).show()
                        }

                    })
```
[GitHup地址](https://github.com/soulqw/SoulPhotoTaker/)