# 襖ブロック仕様

## ブロック構造

1種類のブロック `FusumaPartBlock` が 12 個で1つの襖を構成します。

```
[ 右パネル ][ 左パネル ]
  2×3 個      2×3 個
  ─────────────────────
    合計 4ブロック幅 × 3ブロック高
```

## BlockState プロパティ

| プロパティ | 型 | 値 | 意味 |
|---|---|---|---|
| `FACING` | Direction | NORTH / SOUTH / EAST / WEST | 正面の向き |
| `SIDE` | FusumaSide | LEFT / RIGHT | 左右どちらのパネル |
| `PART_X` | Integer | 0..1 | パネル内の横位置 |
| `PART_Y` | Integer | 0..2 | パネル内の高さ（0=下） |
| `DOOR_STATE` | FusumaOpenState | CLOSED / LEFT_OPEN / RIGHT_OPEN | 開閉状態 |
| `FLIPPED_HORIZONTAL` | Boolean | false / true | 前後チャンネル反転 |

## 操作

| 操作 | 効果 |
|---|---|
| 右クリック（通常） | クリックしたパネルを開閉（CLOSED ↔ LEFT_OPEN または RIGHT_OPEN） |
| スニーク＋右クリック | `FLIPPED_HORIZONTAL` をトグル（前後チャンネルの入れ替え） |

- 片方が開いているときに逆側を右クリックしても何も起きません（どちらかしか開けられない）

## 当たり判定

| 状態 | コリジョン |
|---|---|
| CLOSED | 3/16 厚の薄板（FACING に応じた向き、前後チャンネルはずれた位置） |
| LEFT_OPEN（左パネル） | 空（通行可能） |
| RIGHT_OPEN（右パネル） | 空（通行可能） |

## 設置（FusumaItem）

- プレイヤーの向き → FACING
- クリック位置を起点に、右方向（`getClockWise()`）へ 4 ブロック展開
- 起点を 0〜3 ブロック左にずらした **4 候補**を順番に検証し、最初に置ける位置に設置
- 条件：全 12 位置が replaceable、かつ最下段（PART_Y=0）の下に固体ブロックが存在すること
- スニーク設置 → `FLIPPED_HORIZONTAL=true` で配置
- サバイバルではアイテム 1 個消費

## 破壊

- 1 ブロック破壊 → 残り 11 ブロックも連動して除去
- アイテムドロップは 1 個（コードで制御、ルートテーブルは空プール）
- 非プレイヤー破壊（爆発など）にも対応（BlockState から origin を再構成）

## クラス構成

| ファイル | 役割 |
|---|---|
| `FusumaPartBlock.kt` | ブロック本体（形状・操作・破壊） |
| `FusumaBlockEntity.kt` | origin 座標の永続化（`OriginBlockEntity` を継承） |
| `FusumaItem.kt` | 設置ロジック・候補検証 |
| `FusumaOpenState.kt` | enum: CLOSED / LEFT_OPEN / RIGHT_OPEN |
| `FusumaSide.kt` | enum: LEFT / RIGHT |
| `OriginBlockEntity.kt` | origin を NBT 保存する抽象基底（TatamiBlockEntity と共用） |

## レシピ

```
P P P
  W  
```

- P = 紙（`minecraft:paper`）× 3
- W = 木材（任意の `#minecraft:planks` タグ）× 1
- 紙をインベントリに入れるとレシピブックにアンロック

## テクスチャ

32×32 PNG、12 枚のポジション別タイル + エッジ用 1 枚：

| ファイル名パターン | 対象 |
|---|---|
| `fusuma_sl_x{0-1}_y{0-2}.png` | 左パネル（SIDE=LEFT）各タイル |
| `fusuma_sr_x{0-1}_y{0-2}.png` | 右パネル（SIDE=RIGHT）各タイル |
| `fusuma_edge.png` | 端面（木口）テクスチャ |

- 外枠：木目フレーム（4px 幅）
- 内側：和紙（横繊維ノイズ）
- 引手：左パネル左端中段（`sl_x0_y1`）と右パネル右端中段（`sr_x1_y1`）に円形引手
