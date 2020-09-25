# CoCo
 [![Hex.pm](https://img.shields.io/badge/download-1.0.0-green)](https://www.apache.org/licenses/LICENSE-2.0)
 [![Hex.pm](https://img.shields.io/badge/Api-4.0%2B-yellow)]()
 [![Hex.pm](https://img.shields.io/hexpm/l/plug.svg)]()
 [![Hex.pm](https://img.shields.io/badge/Jetpack-AndroidX-red)]()
#### 一款小而美的的Android系统相机拍照和系统相册选择库🐵
 - 一行代码完成从系统相机拍照或者系统相册选择图片
 - 内部适配 7.0 FileProvider文件处理，无需自己额外处理
 - 默认图片处理器自带两种图片压缩策略，并可按需自定义图片处理器
 - 支持Activity、Fragment,图片异步处理器工作自动绑定相关生命周期
 - 完全基于Kotlin编写，与Java兼容
 - 全面适配AndroidX、配置简单，导入方便
 - 支持debug模式
 - 全新设计的APi-更灵活-更易于理解
## Installation：

 ![image](https://img-blog.csdnimg.cn/20191009181659912.png)

最新版本(Based on Android X):
###### 最新Release 改动：
- 修复了部分资源泄露的问题
- 优化了内存消耗、提高了源码可读性

```java
dependencies {
    implementation 'com.qw:coco:1.0.0'
}
```
CoCo 1.0.0+ 将迎来历史上最大的更新，全新重构的APi，更易于理解，更解耦，更灵活，强烈建议您迁移到最新的APi，方便后续新功能的拓展，老版本最后Release 版本将维护至[0.3.1](https://github.com/soulqw/CoCo/blob/developer/README_OLD.md)，后续不再更新。

## Usage：
#### 基本用法

- 调用系统相机拍照
```kotlin
       CoCo.with(this@MainActivity)
                .take(createSDCardFile())
                .start(object : CoCoCallBack<TakeResult> {

                    override fun onSuccess(data: TakeResult) {
                       iv_image.setImageBitmap(Utils.getBitmapFromFile(data.savedFile!!.absolutePath))
                    }

                    override fun onFailed(exception: Exception) {
                    }
                })
```
- 调用系统相册选择图片：

```kotlin
        CoCo.with(this@MainActivity)
                    .pick()
                    .start(object : CoCoCallBack<PickResult> {
                        override fun onSuccess(data: PickResult) {

                        iv_image.setImageURI(data.originUri)

                        }

                        override fun onFailed(exception: Exception) {
                        }
                    })
```
##### 效果图:

#### 系统相册选择
APi与拍照相似，同样支持压缩，选择照片结果中提供原始Uri
- 仅仅是从系统相册选一张图片：

```kotlin
    CoCo.with(this@MainActivity)
                    .pick()
                    .start(object : CoCoCallBack<PickResult> {
                        override fun onSuccess(data: PickResult) {
                            Toast.makeText(
                                this@MainActivity,
                                data.originUri.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                            iv_image.setImageURI(data.originUri)
                        }

                        override fun onFailed(exception: Exception) {
                        }
                    })
```
- 处理我们拿到的原图：

上述以上是原图的情形，通常情况下，我们常常要对原图做一些处理，比如压缩等，所以CoCo 提供了dispose操作符，方便获得图片之后做一些处理：
```kotlin
        //选择图片后压缩
         CoCo.with(this)
                .pick()
                //切换操作符
                .then()
                .dispose()
                .start(object : CoCoCallBack<DisposeResult> {
                    override fun onSuccess(data: DisposeResult) {
                        iv_image.setImageBitmap(data.compressBitmap)
                    }

                    override fun onFailed(exception: Exception) {
                        Log.d(MainActivity.TAG, exception.toString())
                    }
                })

```
我们通过 then 操作符来完成操作符的组合，可以进行一些列操作符的串联流式处理。

- dispose 操作符

dispose操作符可以让我们在子线程处理我们的文件，并且异步任务自动绑定我们with 操作符的生命周期

###### 它可以单独使用：
```kotlin
        CoCo.with(this)
                .dispose()
                .origin(imageFile.path)
                .start(object : CoCoCallBack<DisposeResult> {

                    override fun onSuccess(data: DisposeResult) {
                        iv_image.setImageBitmap(data.compressBitmap)
                    }

                    override fun onFailed(exception: Exception) {
                        Log.d(MainActivity.TAG, exception.toString())
                    }
                })
```
###### 它也可以组合其它操作符使用：
```
     CoCo.with(this@MainActivity)
                    .take(createSDCardFile())
                    .then()
                    .dispose()
                    .start(object : CoCoCallBack<DisposeResult> {

                        override fun onSuccess(data: DisposeResult) {
                            iv_image.setImageBitmap(Utils.getBitmapFromFile(data.savedFile!!.absolutePath))
                        }

                        override fun onFailed(exception: Exception) {

                        }
                    })
```


自定义压缩策略:

```kotlin
    /**
     * 自定义图片处理器
     * 自定义想要处理的任意结果
     */
    class CustomDisposer : ImageDisposer {

        override fun disposeImage(originPath: String, targetSaveFile: File?): BaseResult {
            return BaseResult().also {
                val bitmap = QualityCompressor()
                    .compress(originPath, 5)
                it.targetFile = targetSaveFile
                it.compressBitmap = bitmap
            }
        }

    }
    
    fun custom() {
        CoCo.with(this)
            .take(createSDCardFile())
            .applyWithDispose(CustomDisposer())
            .start(object : GetImageCallBack<TakeResult> {

                override fun onSuccess(data: TakeResult) {
                    Toast.makeText(this@MainActivity, "自定义Disposer拍照操作最终成功", Toast.LENGTH_SHORT)
                        .show()
                    iv_image.setImageBitmap(data.compressBitmap)
                }

                override fun onFailed(exception: Exception) {
                    Toast.makeText(
                        this@MainActivity,
                        "拍照异常: $exception",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            })
    }
```


### 截图：
![image](https://upload-images.jianshu.io/upload_images/4346197-45eef4367cc55ca1.png)

![image](https://upload-images.jianshu.io/upload_images/4346197-c5b04e7acad92ff3.png)

[GitHub地址](https://github.com/soulqw/SoulPhotoTaker/)
