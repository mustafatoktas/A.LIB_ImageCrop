# Image Crop

Jetpack Compose için görsel kırpma kütüphanesi. Serbest, kare ve şeffaf köşeli daire çıktısı üretir; 90° döndürme ve hazır bir Material 3 ekranı içerir.

## Kurulum

Android 13 (API 33) ve üzeri, Jetpack Compose ve Material 3 gerekir.

**Doğrudan ekleme**

```kotlin
// app/build.gradle.kts
dependencies {
    implementation("com.toktasoft:image-crop:1.0.1")
}
```

**Sürüm kataloğu (TOML) ile**

```toml
# gradle/libs.versions.toml
[versions]
imageCrop = "1.0.1"

[libraries]
image-crop = { group = "com.toktasoft", name = "image-crop", version.ref = "imageCrop" }
```

```kotlin
// app/build.gradle.kts
dependencies {
    implementation(libs.image.crop)
}
```

Compose, Material 3 ve Core KTX bağımlılıkları otomatik gelir. Kütüphane ek bir ikon paketi getirmez.

Proje JDK 25, Gradle 9.8, AGP 9.5 ve Kotlin 2.4.21 ile derlenir. Gradle JDK'sı `gradle/gradle-daemon-jvm.properties` ile 25'e sabitlenmiştir.

## Örnek uygulama

Kütüphaneyi kod yazmadan denemek için örnek uygulamanın APK'sını [Releases](https://github.com/mustafatoktas/A.LIB_ImageCrop/releases/latest) sayfasından indirip kurabilirsiniz. Uygulamadaki ayarlar kartından kırpma şekillerini, kılavuz çizgilerini ve çıktı boyutunu değiştirerek ekranın bütün seçeneklerini canlı görebilirsiniz.

## Hazır ekran

```kotlin
import com.toktasoft.imagecrop.ImageCropScreen

ImageCropScreen(
    bitmap = sourceBitmap,
    onResult = { result ->
        save(result)
        closeEditor()
    },
    onDismiss = { closeEditor() },
)
```

Ekranda kırpma alanı, 90° döndürme butonları, seçimi sıfırlama, kırpma şekli ve kılavuz çizgileri kartları, "Görselin tamamını kullan" butonu ve yüzen Kırp butonu bulunur.

### Parametreler

| Parametre | Tür | Varsayılan | Açıklama |
|---|---|---|---|
| `bitmap` | `Bitmap` | Zorunlu | Kırpılacak görsel. Uygulamanıza aittir; kütüphane değiştirmez veya recycle etmez. |
| `onResult` | `(Bitmap) -> Unit` | Zorunlu | Kırpılan yeni bitmap ile ana iş parçacığında çağrılır. Ekran kendini kapatmaz; ne olacağına uygulama karar verir. |
| `onDismiss` | `() -> Unit` | Zorunlu | Üst çubuktaki geri butonuna basılınca çağrılır. Sistem geri hareketini kütüphane yakalamaz; gerekiyorsa uygulamada `BackHandler` kullanın. |
| `modifier` | `Modifier` | `Modifier` | Ekranın kapsayıcısına uygulanır. |
| `cropShape` | `CropShape` | `SQUARE` | Başlangıç şekli: `FREE`, `SQUARE` veya `CIRCLE`. Şekil kartı gizliyse bu şekil sabittir. |
| `cropShapes` | `List<CropShape>` | Hepsi | Kartta sunulacak şekiller ve sırası. `cropShape` bu listede olmalıdır. |
| `showCropShapeCard` | `Boolean` | `true` | Kırpma şekli kartını gösterir. Listede tek şekil varsa kendiliğinden gizlenir. |
| `showGuideLines` | `Boolean` | `true` | Kılavuz çizgilerinin başlangıç durumu. Kart gizliyse bu değer sabittir. |
| `showGuideLinesCard` | `Boolean` | `true` | Kılavuz çizgileri aç/kapa kartını gösterir. |
| `showUseFullImage` | `Boolean` | `true` | "Görselin tamamını kullan" butonunu gösterir. Bu buton görselin tamamını döndürülmüş haliyle, kırpmadan ve maskesiz verir; sonucun seçilen şekle uyması gerekiyorsa gizleyin. |
| `options` | `CropOptions` | `CropOptions()` | Çıktı boyutu sınırları: `maxWidth` ve `maxHeight` (varsayılan 4096) ile `maxPixels` (varsayılan 16.777.216). Küçük görseller büyütülmez. |
| `onError` | `(Throwable) -> Unit` | `{}` | Dışa aktarma başarısız olursa çağrılır. Ekran ayrıca bir snackbar gösterir. |

### Örnekler

```kotlin
// Profil fotoğrafı her zaman 1:1: şekil seçilemez, tam görsel seçeneği yok.
ImageCropScreen(
    bitmap = sourceBitmap,
    onResult = ::save,
    onDismiss = ::close,
    cropShape = CropShape.SQUARE,
    showCropShapeCard = false,
    showUseFullImage = false,
)

// Gösterim alanı 1:1 ama serbest oranlar da kabul ediliyor: daire sunulmaz.
ImageCropScreen(
    bitmap = sourceBitmap,
    onResult = ::save,
    onDismiss = ::close,
    cropShapes = listOf(CropShape.SQUARE, CropShape.FREE),
)

// Daire profil fotoğrafı; kılavuz çizgileri kapalı ve değiştirilemez.
ImageCropScreen(
    bitmap = sourceBitmap,
    onResult = ::save,
    onDismiss = ::close,
    cropShape = CropShape.CIRCLE,
    showCropShapeCard = false,
    showGuideLines = false,
    showGuideLinesCard = false,
    showUseFullImage = false,
)
```

## Kendi arayüzünüz

```kotlin
import com.toktasoft.imagecrop.CropOptions
import com.toktasoft.imagecrop.CropShape
import com.toktasoft.imagecrop.ImageCropper
import com.toktasoft.imagecrop.rememberImageCropState

val state = rememberImageCropState(sourceBitmap, cropShape = CropShape.FREE, aspectRatio = 16f / 9f)
val scope = rememberCoroutineScope()

ImageCropper(state, Modifier.fillMaxWidth().height(360.dp))

Button(enabled = state.canCrop, onClick = {
    scope.launch { save(state.crop(CropOptions(maxWidth = 1920, maxHeight = 1080))) }
}) { Text("Kırp") }
```

**`ImageCropState`**

| Üye | Açıklama |
|---|---|
| `cropShape`, `aspectRatio` | Değiştirilebilir. Değişince seçim sıfırlanır. `aspectRatio` yalnızca `FREE` için geçerlidir; `null` serbest seçim demektir. |
| `rotationDegrees` | 0, 90, 180 veya 270. |
| `canCrop` | Kırpıcı yerleştikten sonra `true` olur. |
| `rotateClockwise()`, `rotateCounterClockwise()`, `reset()` | Döndürme seçimi sıfırlar. |
| `crop(options)` | Seçimi yeni bir bitmap olarak `Dispatchers.Default` üzerinde üretir. |
| `fullImage(options)` | Görselin tamamını döndürülmüş haliyle, kırpmadan ve maskesiz üretir. |

**`ImageCropper`** boyutunu `modifier`'dan alır. Görünümü `ImageCropperStyle` ile ayarlanır:

| Alan | Varsayılan | Açıklama |
|---|---|---|
| `lineColor` | Açık lila | Kenar, kılavuz ve tutamaç rengi. |
| `overlayColor` | %55 siyah | Seçim dışında kalan fotoğrafın üstüne çizilir. |
| `borderWidth` | 1.5 dp | Seçim kenarı. |
| `guideLineWidth` | 1 dp | 3×3 kılavuz çizgileri. |
| `handleStyle` | `BRACKETS` | `BRACKETS`: köşe ve kenar ortası tutamaçları. `DOTS`: köşe noktaları. |
| `handleWidth` | 2 dp | `BRACKETS` kalınlığı. |
| `dotRadius` | 6 dp | `DOTS` yarıçapı. |

Seçim fotoğrafın dışına çıkamaz. Fotoğrafın yanındaki boşluk karartılmaz; arkadaki zemin görünür. Kendi arayüzünüzde coroutine hatalarını yakalayın ve işlem sürerken tekrar tıklamayı engelleyin.

## Görsel yükleme

Android'in Photo Picker'ından dönen URI ile:

```kotlin
val bitmap = ImageCropLoader.load(context, uri)
```

- **Çalışma şekli:** `suspend` bir fonksiyondur ve IO iş parçacığında çalışır. EXIF yönünü uygular ve yazılımsal bir bitmap döndürür.
- **Boyut sınırı:** Büyük görseller doğrudan sınır içinde açılır; varsayılan sınır uzun kenarda 4096 piksel ve toplamda 16.777.216 pikseldir. Sınırlar `maxDimension` ve `maxPixels` ile değiştirilebilir.
- **Hata durumu:** Bozuk veya erişilemeyen görsel hata fırlatır. Coroutine iptali yutulmaz.

## Bellek ve çıktı

- **Kaynak bitmap:** Uygulamanıza aittir. Kütüphane onu değiştirmez veya recycle etmez; kullanım sürerken siz de recycle etmeyin.
- **Çıktı:** Her seferinde yeni bir bitmap'tir ve uygulamanıza aittir. Varsayılan sınır 4096 × 4096 ve 16.777.216 pikseldir; küçük görseller büyütülmez.
- **Daire çıktısı:** Kare boyutludur ve köşeleri şeffaftır. Şeffaflığı korumak için PNG gibi alfa destekleyen bir biçimde kaydedin.
- **Durum kaydı:** `rememberImageCropState` şekli, oranı, döndürmeyi ve seçimi kaydeder; bitmap'i ve URI iznini kaydetmez. Activity veya süreç yeniden oluşunca kaynağı tekrar sağlamak uygulamanın işidir.
- **Kapsam dışı:** Pinch zoom, serbest açılı düzeltme ve çok büyük orijinallerin tam çözünürlükte bölgesel kırpılması bu sürümde yok.

## Lisans

GNU GPL v3. Ayrıntılar için `License` dosyasına bakın.

## Toktasoft
