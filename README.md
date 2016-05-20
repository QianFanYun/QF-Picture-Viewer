# QF-Picture-Viewer
QF Open Source High imitation WeChat picture browser view

#DEMO
![demo.gif](https://github.com/QianFanYun/QF-Picture-Viewer/blob/master/demo.gif)

### GallaryImageSelectActivity
这是相册图片浏览和选择的类

* mMaxImageSelected 用来设置最多可以选择图片数量
* mDurationTime 所有图片选择的相册目录弹出动画时间
* mAllImageTitle 左下角显示的所有图片的title，可以根据自己需要设置

### GallaryImageView
图片的加载方式在这个类里面实现，默认使用Glide来加载本地图片，如果需要修改加载方式，可以在 ***setImageUri*** 方法中修改

* setImageUri 加载图片方式，重载后可以通过 ***path*** 和 ***File*** 来加载图片 
