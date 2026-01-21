# TODO应用 README

## 项目简介

TODO是一款简洁高效的任务管理应用，帮助用户轻松组织和跟踪日常任务。应用采用现代化的Jetpack Compose UI框架，提供流畅的用户体验和直观的任务管理功能。

## 功能特点

- 📝 **任务管理**：支持添加、编辑、删除任务，包含标题、描述和截止时间
- 📊 **任务状态**：任务分为待办、已完成、已删除三种状态，可在不同页面查看
- 🗑️ **废纸桶**：已删除的任务会进入废纸桶，支持恢复操作
- 📈 **统计分析**：提供任务完成率和最近7天任务分布的可视化图表
- 🔄 **状态转换**：采用先勾选后操作的交互方式，提高用户操作体验
- 🎨 **现代化UI**：使用Jetpack Compose构建，界面美观流畅

## 技术栈

| 类别 | 技术/框架 | 版本 |
|------|-----------|------|
| 开发语言 | Kotlin | 1.9.23 |
| UI框架 | Jetpack Compose | 2024.03.00 |
| 数据库 | Room | 2.6.1 |
| 图表库 | MPAndroidChart | v3.1.0 |
| 协程 | Kotlin Coroutines | 内置 |
| 日期时间 | java.time | 内置 |

## 项目结构

```
TODO/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/todo/
│   │   │   │   ├── data/                # 数据层
│   │   │   │   │   ├── Task.kt          # 任务数据模型
│   │   │   │   │   ├── TaskDao.kt       # 数据访问接口
│   │   │   │   │   ├── TaskDatabase.kt  # 数据库定义
│   │   │   │   │   ├── TaskRepository.kt # 仓库层
│   │   │   │   │   └── LocalDateTimeConverter.kt # 日期时间转换器
│   │   │   │   ├── ui/                  # UI层
│   │   │   │   │   ├── components/       # 可复用组件
│   │   │   │   │   │   ├── TaskItem.kt  # 任务卡片
│   │   │   │   │   │   ├── TaskDialog.kt # 任务编辑对话框
│   │   │   │   │   │   └── DateTimePicker.kt # 日期时间选择器
│   │   │   │   │   ├── navigation/       # 导航
│   │   │   │   │   │   └── MainNavigation.kt # 主导航
│   │   │   │   │   └── screens/          # 页面
│   │   │   │   │       ├── PendingTasksScreen.kt # 待办页面
│   │   │   │   │       ├── CompletedTasksScreen.kt # 已完成页面
│   │   │   │   │       ├── StatsScreen.kt # 统计页面
│   │   │   │   │       └── TrashScreen.kt # 废纸桶页面
│   │   │   │   └── MainActivity.kt       # 主活动
│   │   │   └── res/                      # 资源文件
│   │   │       ├── mipmap-anydpi-v26/    # 图标
│   │   │       ├── values/               # 字符串、颜色等
│   │   │       └── xml/                  # 配置文件
│   │   └── test/                         # 测试代码
│   └── build.gradle                      # 应用构建配置
├── build.gradle                          # 项目构建配置
└── settings.gradle                       # 项目设置
```

## 安装说明

### 前提条件

- Android Studio Arctic Fox (2020.3.1) 或更高版本
- Android SDK API Level 24 或更高
- Kotlin 1.9.0 或更高版本
- JDK 17 或更高版本

### 安装步骤

1. 克隆项目仓库：
   ```bash
   git clone https://github.com/yourusername/todo-app.git
   ```

2. 在Android Studio中打开项目：
   - 启动Android Studio
   - 点击"Open an existing project"
   - 选择项目目录并点击"OK"

3. 同步项目依赖：
   - 等待Android Studio自动同步Gradle依赖
   - 或点击"File" > "Sync Project with Gradle Files"

4. 构建并运行项目：
   - 连接Android设备或启动模拟器
   - 点击"Run" > "Run 'app'"
   - 选择目标设备并点击"OK"
##手机用户
1.目前只适配安卓手机，安卓用户可以下载项目的app/build/outputs/apk/release/app-release-unsigned.apk，然后在手机中安装。
2.如果上面apk下载失败，可以试试：

## 使用指南

### 基本操作

1. **添加任务**：
   - 在任何页面点击右下角的"+"按钮
   - 填写任务标题、描述和截止时间
   - 点击"保存"按钮

2. **编辑任务**：
   - 在任务卡片上点击编辑按钮
   - 修改任务信息
   - 点击"保存"按钮

3. **删除任务**：
   - 在任务卡片上点击删除按钮
   - 任务会被移至废纸桶

4. **完成任务**：
   - 在待办页面勾选任务
   - 点击"完成"按钮
   - 任务会被移至已完成页面

5. **恢复任务**：
   - 在已完成页面勾选任务
   - 点击"恢复"按钮
   - 任务会被移回待办页面

6. **管理废纸桶**：
   - 点击底部导航栏的"废纸桶"图标
   - 勾选任务并点击"恢复"按钮可恢复任务
   - 点击删除按钮可永久删除任务

### 统计分析

- 点击底部导航栏的"统计"图标
- 查看任务总数、已完成数、待办数和完成率
- 查看最近7天任务分布的柱状图

## 技术实现

### 数据持久化

使用Room数据库实现本地数据存储：
- 定义Task实体类表示任务数据
- 使用TaskDao接口定义数据访问方法
- 通过TaskRepository封装数据操作逻辑
- 使用LocalDateTimeConverter处理日期时间类型

### UI构建

使用Jetpack Compose构建现代化UI：
- 使用Composable函数构建可复用组件
- 使用MutableState和remember管理UI状态
- 使用LaunchedEffect和CoroutineScope处理异步操作
- 使用Modifier系统实现布局和样式

### 状态管理

实现清晰的任务状态管理：
- 使用isCompleted和isDeleted字段标记任务状态
- 在不同页面使用不同的数据库查询获取对应状态的任务
- 实现状态转换方法，如markAsDeleted、restoreTask等
- 使用CoroutineScope在后台线程执行数据库操作

### 图表实现

使用MPAndroidChart库实现数据可视化：
- 计算最近7天每天的任务数量
- 配置图表样式、标签和动画效果
- 使用AndroidView在Compose中集成传统View

## 性能优化

- 使用CoroutineScope在后台线程执行数据库操作
- 使用MutableStateListOf实现高效的列表更新
- 使用remember和rememberCoroutineScope优化状态管理
- 合理使用Compose的重组机制，避免不必要的UI更新

## 贡献指南

1. Fork项目仓库
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开Pull Request

## 许可证

本项目采用MIT许可证 - 详情请参阅LICENSE文件

## 联系方式

- 项目链接：[https://github.com/yourusername/todo-app](https://github.com/ailiabudureheman/ToDo)
- 作者：艾力
- 邮箱：3368006636@qq.com

## 致谢

- [Jetpack Compose](https://developer.android.com/jetpack/compose) - 现代化UI框架
- [Room](https://developer.android.com/training/data-storage/room) - 本地数据库解决方案
- [MPAndroidChart](https://github.com/PhilJay/MPAndroidChart) - 图表库
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) - 异步编程
- [java.time](https://docs.oracle.com/javase/8/docs/api/java/time/package-summary.html) - 日期时间处理

---

感谢使用TODO应用！希望它能帮助你更有效地管理任务，提高工作效率。
