# Facade Pattern (Yapısal / Structural)

## Niyet (Intent)

Facade, karmaşık bir kütüphane/çatı (framework) veya alt sistem için **basitleştirilmiş bir arayüz** sunar.

---

## Problem

Bazen kodumuzun, çok sayıda sınıftan oluşan karmaşık bir sisteme bağlanması gerekir.
Normalde istemci tarafında şunları yapmak zorunda kalırız:

- Birçok nesneyi doğru sırayla oluşturmak,
- Bağımlılıkları takip etmek,
- Metotları doğru akışta çağırmak,
- Format dönüşümleriyle uğraşmak.

Bu durumda iş kuralı kodu, 3. parti alt sistem detaylarına aşırı bağımlı hale gelir; okunabilirlik ve bakım maliyeti düşer.

---

## Çözüm

Facade, alt sistemin tüm karmaşasını tek bir sade giriş noktasında toplar.

- İstemci sadece facade ile konuşur,
- Facade gerekli alt sınıfları içeride koordine eder,
- İstemci artık alt sistemin iç detaylarını bilmek zorunda kalmaz.

Trade-off: Facade genelde alt sistemin tüm özelliklerini açmaz, sadece istemcinin sık kullandığı kısmı verir.

---

## Projedeki OOP Örneği (Video Dönüştürme)

Bu projede Facade örneği video dönüştürme senaryosu üzerinden kuruldu.

### Alt sistem sınıfları
- `VideoFile`
- `CodecFactory`
- `Mpeg4CompressionCodec`
- `OggCompressionCodec`
- `BitrateReader`
- `AudioMixer`

### Facade sınıfı
- `VideoConverterFacade`

### Client
- `FacadePatternDemo`

`FacadePatternDemo`, onlarca adımı bilmek yerine yalnızca şu çağrıyı yapar:

```java
ConvertedFile file = converter.convert("funny-cats-video.ogg", "mp4");
```

Bu tek çağrının içinde facade şunları yapar:
1. Kaynak dosyayı analiz eder (`VideoFile`, `CodecFactory`),
2. Hedef codec’i seçer,
3. Bitrate okuma + dönüştürme adımlarını yürütür,
4. Ses düzeltmesini uygular (`AudioMixer`),
5. Sonucu `ConvertedFile` olarak döner.

---

## Explain (Türkçe Açıklama)

- Facade’ın amacı, "karmaşık sistemi gizleyip sade bir API vermek"tir.
- Alt sınıflar facade’ı bilmez; kendi aralarında doğrudan çalışmaya devam eder.
- İstemci sadece facade’a bağımlı kaldığı için, alttaki sistem değiştiğinde çoğu zaman yalnızca facade güncellenir.
- Bu sayede bağımlılık azalır, entegrasyon kolaylaşır, bakım maliyeti düşer.

Bu projede de istemci tarafı `CodecFactory`, `BitrateReader`, `AudioMixer` gibi sınıfları tek tek kullanmıyor; tüm akış `VideoConverterFacade` içinde orkestre ediliyor.

---

## Gerçek Hayat Analojisi

Telefonla sipariş verirken operatör, mağazanın tüm departmanları (stok, ödeme, kargo) için facade görevi görür.
Müşteri tek bir noktayla konuşur; içerideki karmaşık süreçler arka planda yürür.

---

## Uygulanabilirlik

Facade kullan:
- Karmaşık bir alt sisteme sade bir giriş noktası gerektiğinde,
- İstemciyi alt sistem detaylarından izole etmek istediğinde,
- Katmanlı mimaride her katman için net giriş/çıkış noktaları tanımlamak istediğinde.

---

## Artılar / Eksiler

### Artılar
- Karmaşıklığı gizler.
- İstemci ile alt sistem arasında gevşek bağlılık sağlar.
- Entegrasyon ve sürüm yükseltmelerini kolaylaştırır.

### Eksiler
- Zamanla büyüyüp "god object" haline gelebilir.
- Aşırı sadeleştirme bazı gelişmiş özelliklere erişimi zorlaştırabilir.

---

## Kısa Özet

Facade, "karmaşık bir sistemi tek kapıdan kullanma" desenidir.
Bu projede `VideoConverterFacade`, video dönüştürme alt sistemini saklayıp istemciye tek metotla (`convert`) sade bir kullanım sunar.
