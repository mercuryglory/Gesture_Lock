# 手势密码
* 为满足项目需要，新改造的手势密码允许跨越式选中，比如第一竖列不再只能在1-4-7的情况，在1-7-4的情况下也能选中。
* 只做了九宫格样式，网上有自定义控件可传入参数，4X4,5X5以及更多。我觉得移动端屏幕有限的情况下没有太大实际意义，反而会引发误操作，因此这里内部是硬编码。你可以自行clone修改。
* ViewGroup和View还要进一步解耦，ViewGroup（手势密码）提供回调方法，而不是初始化的时候创建回调方法的对象。
* 自定义属性

效果如下：

![使用Python在Markdown插入图片并自动获取链接_手势密码.gif](http://om2doplmh.bkt.clouddn.com/使用Python在Markdown插入图片并自动获取链接_手势密码.gif)
