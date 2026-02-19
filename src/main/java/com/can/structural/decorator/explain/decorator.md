# Decorator Pattern (Yapısal / Structural)

Diğer adı: **Wrapper**

## Niyet (Intent)

Decorator, bir nesnenin davranışını sınıfı değiştirmeden, onu saran (wrap eden) nesnelerle runtime’da genişletmeyi sağlar.

Yani:
- aynı arayüz korunur,
- yeni özellikler katman katman eklenir,
- kalıtım yerine kompozisyon kullanılır.

---

## Problem (Bildirim örneği)

Başlangıçta sadece e-posta gönderen bir `Notifier` sınıfın var.

Sonra ihtiyaçlar büyüyor:
- SMS,
- Facebook,
- Slack,
- hatta birden fazla kanal aynı anda.

Bunu kalıtımla çözmeye çalışınca şu olur:
- `EmailNotifier`
- `EmailSmsNotifier`
- `EmailSlackNotifier`
- `EmailSmsSlackNotifier`
- ...

Yani kombinasyon arttıkça sınıf sayısı patlar (**combinatorial explosion**).

---

## Çözüm

Decorator yaklaşımında:
1. Ortak bir `Notifier` arayüzü tanımlanır.
2. Temel davranış `EmailNotifier` gibi bir somut sınıfta tutulur.
3. `BaseNotifierDecorator`, içinde başka bir `Notifier` referansı taşır ve çağrıyı ona delege eder.
4. `SmsDecorator`, `SlackDecorator`, `FacebookDecorator` gibi sınıflar davranışı **önce/sonra** genişletir.

İstemci, nesneyi ihtiyaç kadar katmanla sarar:
- `SlackDecorator(SmsDecorator(EmailNotifier))`

Böylece tek bir stack ile çoklu kanal davranışı elde edilir.

---

## Projedeki OOP örneği

Bu projede roller:

- **Component**: `Notifier`
- **Concrete Component**: `EmailNotifier`
- **Base Decorator**: `BaseNotifierDecorator`
- **Concrete Decorators**: `SmsDecorator`, `SlackDecorator`, `FacebookDecorator`
- **Client**: `DecoratorPatternDemo`

`DecoratorPatternDemo` içinde iki farklı stack kuruluyor:
1. `Email -> SMS -> Slack`
2. `Email -> Facebook`

Client kodu her durumda yalnızca `Notifier` tipini görüyor; hangi kanalların aktif olduğu stack ile belirleniyor.

---

## Neden Decorator?

Çünkü ihtiyaç **davranışı runtime’da birleştirmek**.

- Inheritance statiktir, mevcut nesneyi dinamik genişletmek zordur.
- Decorator ile davranış katmanları tak-çıkar yapılabilir.
- Interface aynı kaldığı için istemci kodu değişmez.

---

## Artılar / Eksiler

### Artılar
- Yeni subclass açmadan davranış genişletme.
- Runtime’da sorumluluk ekleyip çıkarabilme.
- Birden fazla davranışı üst üste birleştirme.
- SRP: her decorator tek bir ek sorumluluğa odaklanır.

### Eksiler
- Katman sırasına bağlı davranışlar zorlaşabilir.
- Belirli bir decorator’ı aradan çıkarmak zor olabilir.
- Konfigürasyon kodu (stack kurma) kalabalık görünebilir.

---

## Kısa Özet

Decorator, nesnenin “içini” değil “dışını” katmanlar. Bu projede `Notifier` akışı üzerine SMS/Slack/Facebook katmanları eklenerek, aynı arayüzle farklı bildirim kombinasyonları runtime’da kuruluyor.
