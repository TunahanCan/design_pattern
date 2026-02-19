# Bridge Pattern (Yapısal / Structural)

## Niyet (Intent)

Bridge, bir sınıfı iki bağımsız eksene ayırır:

- **Abstraction (Soyutlama)**: istemcinin kullandığı üst seviye taraf
- **Implementation (Uygulama/Platform)**: gerçek işi yapan alt seviye taraf

Amaç, bu iki tarafın birbirinden bağımsız gelişebilmesidir.

---

## Problem

Sistemde iki farklı değişim ekseni olduğunda kalıtım patlaması olur.

Örn:
- Cihaz türleri: `TV`, `Radio`
- Kumanda türleri: `BasicRemote`, `AdvancedRemote`

Eğer her kombinasyonu kalıtımla çözmeye çalışırsak:
- `TvBasicRemote`, `TvAdvancedRemote`, `RadioBasicRemote`, `RadioAdvancedRemote` ...

Yeni cihaz veya yeni kumanda geldiğinde sınıf sayısı hızla artar.

---

## Çözüm

Bridge, bu iki ekseni kompozisyon ile ayırır:

- Kumanda sınıfları bir `Device` referansı taşır.
- Kumanda, işi doğrudan yapmak yerine cihaza delege eder.

Böylece:
- Yeni kumanda eklemek için cihazları değiştirmezsin.
- Yeni cihaz eklemek için kumandaları değiştirmezsin.

---

## Projedeki OOP Örneği

Bu projede Bridge şu şekilde kuruldu:

### Implementation tarafı
- `Device` arayüzü ortak işlemleri tanımlar (`enable`, `setVolume`, `setChannel`...).
- `Tv` ve `Radio`, `Device` arayüzünü uygular.

### Abstraction tarafı
- `RemoteControl` temel kumanda davranışlarını içerir (`togglePower`, `volumeUp`, `channelUp`...).
- `AdvancedRemoteControl`, `RemoteControl` sınıfını genişletir ve `mute` özelliği ekler.

### Köprü (Bridge) nerede?

`RemoteControl` içindeki `Device device` alanı köprüdür.
Abstraction ile implementation bu referans üzerinden bağlanır.

---

## Demo akışı (BridgePatternDemo)

1. `RemoteControl` + `Tv` ile temel işlemler çalıştırılır.
2. `AdvancedRemoteControl` + `Radio` ile gelişmiş işlemler (`mute`) çalıştırılır.
3. Aynı gelişmiş kumanda runtime'da `switchDevice(tv)` ile TV'ye geçirilir.

Bu adım, Bridge'in önemli avantajını gösterir: **implementasyonu çalışma anında değiştirebilme**.

---

## Neden bu örnek Bridge?

Çünkü burada amaç algoritma seçmek değil, iki bağımsız hiyerarşiyi ayırmaktır:

- Kumanda çeşitleri (abstraction hiyerarşisi)
- Cihaz çeşitleri (implementation hiyerarşisi)

İkisi birbirinden bağımsız büyür.

---

## Artılar / Eksiler

### Artılar
- Open/Closed: yeni kumanda ve yeni cihazlar kolayca eklenir.
- Single Responsibility: kumanda mantığı ile cihaz detayı ayrılır.
- Kombinasyon patlaması engellenir.

### Eksiler
- Küçük ve tek eksenli problemlerde gereksiz karmaşıklık yaratabilir.

---

## Kısa Özet

Bridge, "iki bağımsız değişim eksenini kalıtımla çarpıp çoğaltmak" yerine,
kompozisyonla birbirine bağlar.
Bu projede `RemoteControl` (abstraction) ile `Device` (implementation) ayrımı,
Bridge pattern'in özünü net şekilde gösterir.
