# IRRemote - 红外遥控器APP

一个通用的Android红外遥控器应用，支持自定义配置和JSON导入。

## 功能特性

- ✅ 支持红外发射功能
- ✅ 支持多种红外协议（NEC、RC5、RC6等）
- ✅ 支持JSON配置导入
- ✅ 支持手动编辑遥控器配置
- ✅ 支持多设备管理
- ✅ Material Design 3 界面
- ✅ 支持Android 7.0+

## 项目结构

```
app/src/main/java/com/example/irremote/
├── data/                    # 数据层
│   ├── local/              # 本地数据库
│   │   ├── db/             # Room数据库
│   │   └── entity/         # 实体类
│   └── repository/         # 数据仓库
├── domain/                  # 领域层
│   ├── model/              # 业务模型
│   └── usecase/            # 用例
├── presentation/            # 表现层
│   ├── ui/                 # 界面组件
│   └── viewmodel/          # ViewModel
├── di/                      # 依赖注入
└── util/                    # 工具类
    ├── IrTransmitter.kt    # 红外发射器
    ├── NecEncoder.kt       # NEC协议编码器
    └── JsonImporter.kt     # JSON导入工具
```

## 快速开始

### 1. 使用内置品牌库

APP内置了多个主流空调品牌的遥控器配置，包括：

**国产一线品牌**：格力、美的、海尔、海信、TCL、长虹、奥克斯、志高、科龙、格兰仕

**国际品牌**：松下、大金、三菱电机、三星、LG、富士通、夏普、东芝、日立、三洋

**其他品牌**：惠而浦、开利、特灵、伊莱克斯、美博、小天鹅、新科、美格、奥力

**品牌集团**：格力集团、美的集团、海尔集团、海信集团、TCL集团、长虹集团、科龙集团、格兰仕集团

**子品牌**：长虹美菱、海信科龙、奥克斯集团

使用步骤：
1. 打开APP
2. 点击右下角"+"按钮
3. 选择品牌 → 选择型号
4. 自动导入配置

### 2. 导入JSON配置

1. 打开APP
2. 点击右上角菜单 → "导入JSON配置"
3. 粘贴JSON配置内容
4. 点击"导入"

### 3. 使用示例配置

项目提供了示例配置文件 `examples/mbo_ac_remote.json`，可以直接导入测试。

### 4. 自定义配置

JSON配置格式：

```json
{
  "name": "遥控器名称",
  "brand": "品牌名称",
  "type": "设备类型",
  "protocol": "NEC",
  "address": "0x00FF",
  "frequency": 38000,
  "gridColumns": 4,
  "buttons": [
    {
      "id": "按钮ID",
      "label": "按钮名称",
      "icon": "图标名称",
      "command": "0x45",
      "row": 0,
      "col": 0
    }
  ]
}
```

## 配置字段说明

### 基本字段

| 字段 | 类型 | 说明 |
|------|------|------|
| name | string | 遥控器名称 |
| brand | string | 品牌名称（可选） |
| type | string | 设备类型：tv, ac, fan, stb等 |
| protocol | string | 红外协议：NEC, RC5, RC6, RAW |
| address | string | 设备地址（十六进制） |
| frequency | number | 红外频率（Hz），默认38000 |
| gridColumns | number | 网格列数，默认4 |

### 按钮字段

| 字段 | 类型 | 说明 |
|------|------|------|
| id | string | 按钮唯一标识 |
| label | string | 显示文本 |
| icon | string | 图标名称（可选） |
| command | string | 命令码（十六进制） |
| row | number | 行位置 |
| col | number | 列位置 |
| rowSpan | number | 占用行数（默认1） |
| colSpan | number | 占用列数（默认1） |

## 支持的图标

- power - 电源
- arrow_up/down/left/right - 方向键
- ok/confirm - 确认
- menu - 菜单
- back/return - 返回
- home - 主页
- volume_up/down - 音量
- mute - 静音
- channel_up/down - 频道
- settings - 设置
- input - 输入源
- play/pause/stop - 播放控制
- ac_unit - 制冷
- mode - 模式
- fan - 风速
- swing - 摆风
- timer - 定时

## 品牌库说明

### 内置品牌列表

APP内置了100+个空调品牌的遥控器配置，每个品牌通常包含多个型号。

### 品牌库结构

```
app/src/main/assets/brands/
├── gree.json           # 格力
├── midea.json          # 美的
├── haier.json          # 海尔
├── hisense.json        # 海信
├── tcl.json            # TCL
├── changhong.json      # 长虹
├── aux.json            # 奥克斯
├── chigo.json          # 志高
├── kelon.json          # 科龙
├── galanz.json         # 格兰仕
├── panasonic.json      # 松下
├── daikin.json         # 大金
├── mitsubishi.json     # 三菱电机
├── samsung.json        # 三星
├── lg.json             # LG
├── fujitsu.json        # 富士通
├── sharp.json          # 夏普
├── toshiba.json        # 东芝
├── hitachi.json        # 日立
├── sanyo.json          # 三洋
├── whirlpool.json      # 惠而浦
├── carrier.json        # 开利
├── trane.json          # 特灵
├── electrolux.json     # 伊莱克斯
├── mbo.json            # 美博
└── ...                 # 更多品牌
```

### 品牌配置格式

每个品牌配置文件包含：
- 品牌信息（中英文名称）
- 设备类型
- 默认协议和地址
- 多个型号的配置

```json
{
  "brand": "格力",
  "brandEn": "Gree",
  "type": "ac",
  "protocol": "NEC",
  "address": "0x00FF",
  "frequency": 38000,
  "models": [
    {
      "name": "格力通用",
      "code": "gree_universal",
      "description": "适用于大部分格力空调",
      "config": { ... }
    }
  ]
}
```

### 添加新品牌

1. 在 `app/src/main/assets/brands/` 目录下创建新的JSON文件
2. 按照上述格式编写配置
3. 重新编译APP即可生效

## 获取红外编码

由于美博空调是小品牌，获取编码的推荐方法：

1. **购买万能遥控器**（众合等品牌）
2. **匹配空调**（使用自动搜索或品牌代码）
3. **录制信号**（使用红外接收模块 + Arduino/树莓派）
4. **导入配置**（将录制的信号转换为JSON格式）

## 开发环境

- Android Studio Hedgehog 或更高版本
- Kotlin 1.9.0
- Jetpack Compose
- Room 数据库
- Hilt 依赖注入

## 构建运行

1. 用Android Studio打开项目
2. 同步Gradle依赖
3. 连接支持红外的Android设备
4. 运行项目

## 注意事项

- 需要支持红外发射的Android设备
- 需要 `TRANSMIT_IR` 权限
- 不同空调的红外编码可能不同，需要实际录制

## License

MIT License
