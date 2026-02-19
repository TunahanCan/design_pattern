# Mediator Pattern (Arabulucu Deseni)

**Diğer adları:** Intermediary, Controller  
**Tür:** Behavioral (Davranışsal)

## Amaç (Intent)

Mediator, nesneler arasındaki karmaşık ve doğrudan bağımlılıkları azaltır. Bileşenler birbirini doğrudan çağırmak yerine tek bir **mediator** nesnesi üzerinden haberleşir.

Bu sayede:

- Bileşenler birbirinin sınıfını bilmez,
- Değişikliklerin etkisi izole olur,
- Yeniden kullanım kolaylaşır.

---

## Problem

UI veya iş akışı senaryolarında bileşenler büyüdükçe birbirine bağlanır:

- Checkbox seçimi başka alanı açar/kapatır,
- Buton tıklaması birden fazla alanı doğrular,
- Her alan diğer alanların durumuna göre davranır.

Bu kurallar bileşen sınıflarına dağıtıldığında:

- Sınıflar sıkı bağlı olur,
- Test etmesi ve bakımı zorlaşır,
- Bileşeni farklı bir ekranda tekrar kullanmak zorlaşır.

---

## Çözüm

Tüm koordinasyon kurallarını **Concrete Mediator** içinde toplarız.

- Bileşenler sadece `mediator.notify(...)` çağırır.
- Kararı mediator verir: hangi durumda hangi bileşen nasıl tepki verecek.
- Bileşenler arası doğrudan çağrı kaldırılır.

---

## Projedeki OOP Örneği

Bu projede örnek bir kimlik doğrulama ekranı üzerinden gösterildi:

- `AuthenticationDialog` → **Concrete Mediator**
- `Mediator` → mediator arayüzü
- `Component` → ortak temel sınıf
- `Checkbox`, `Textbox`, `Button`, `Label` → concrete component
- `MediatorPatternDemo` → örnek akış

### Akış

1. Kullanıcı login/register modunu `Checkbox` ile değiştirir.
2. `Checkbox`, mediator'a `check` olayı gönderir.
3. `AuthenticationDialog`, başlığı ve email alanının görünürlüğünü günceller.
4. Kullanıcı `Tamam` butonuna basınca `click` olayı mediator'a gider.
5. Mediator, aktif moda göre doğrulama yapar ve sonucu `resultMessage` üzerinde üretir.

---

## Neden bu örnek Mediator?

Çünkü `Button`, `Textbox`, `Checkbox` birbirini çağırmıyor. Hepsi yalnızca mediator'a konuşuyor.

- `Button`, “username boş mu?” gibi kural bilmez.
- `Checkbox`, “email alanını göster/gizle” kuralını bilmez.
- Bu kuralların tamamı `AuthenticationDialog` içinde merkezileşir.

Bu da pattern’in özünü birebir sağlar: **merkezi koordinasyon + düşük bağlılık**.

---

## Artılar / Eksiler

### Artılar

- Bağımlılık azalır (low coupling)
- Değişiklik tek noktada yönetilir
- Bileşenler farklı mediator ile yeniden kullanılabilir

### Eksiler

- Mediator zamanla büyüyüp “God Object” olabilir
- Çok fazla senaryoda mediator karmaşıklaşabilir

---

## Ne zaman kullanılmalı?

- Bir grup sınıf birbirine aşırı bağlıysa,
- Bir bileşeni başka bağlamda yeniden kullanmak istiyorsan,
- İletişim kuralları tek merkezde toplanmalıysa,

Mediator iyi bir seçimdir.
