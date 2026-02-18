# Abstract Factory (Creational Pattern)

> Diğer adı: **Kit of Factories / Family Factory**

## Niyet (Intent)
Abstract Factory, birbiriyle ilişkili ürün ailesini (ör. Button + Checkbox) tek noktadan ve tutarlı şekilde üretir.

Kısa versiyon: **"Ürünleri tek tek değil, uyumlu aile olarak üret."**

## Problem
Tema/platform bazlı UI veya entegrasyon katmanında:
- Ürünler birbiriyle uyumlu olmalıdır.
- Yanlış kombinasyonlar (DarkButton + LightCheckbox) kalite sorununa yol açar.
- Client kodu somut sınıflara bağımlı olursa tema değişimi maliyetli olur.

## Çözüm
`GuiFactory` arayüzüyle ürün ailesi sözleşmesi tanımlanır:
- `createButton()`
- `createCheckbox()`

`LightThemeFactory` ve `DarkThemeFactory` bu aileyi tema bazında üretir.
Client (`UiScreen`) sadece factory sözleşmesini bilir.

## Yapı

```mermaid
classDiagram
    direction LR

    class GuiFactory {
      <<interface>>
      +createButton() Button
      +createCheckbox() Checkbox
    }

    class Button {
      <<interface>>
      +render() String
    }

    class Checkbox {
      <<interface>>
      +render() String
    }

    GuiFactory <|.. LightThemeFactory
    GuiFactory <|.. DarkThemeFactory

    Button <|.. LightButton
    Button <|.. DarkButton

    Checkbox <|.. LightCheckbox
    Checkbox <|.. DarkCheckbox

    UiScreen --> GuiFactory
```

## UI tema akışı

```mermaid
flowchart LR
    T[Theme: LIGHT / DARK] --> P[GuiFactoryProvider.forTheme]
    P --> LF[LightThemeFactory]
    P --> DF[DarkThemeFactory]
    LF --> LB[LightButton]
    LF --> LC[LightCheckbox]
    DF --> DB[DarkButton]
    DF --> DC[DarkCheckbox]
    LB --> UI[UiScreen]
    LC --> UI
    DB --> UI
    DC --> UI
```

## Bu projedeki model
- `Button`, `Checkbox` → Abstract Product
- `LightButton`, `DarkButton`, `LightCheckbox`, `DarkCheckbox` → Concrete Product
- `GuiFactory` → Abstract Factory
- `LightThemeFactory`, `DarkThemeFactory` → Concrete Factory
- `GuiFactoryProvider` → Factory seçici
- `UiScreen` → Client

## Teknik notlar
- `GuiFactoryProvider` tema seçimini merkezi bir policy noktasına çeker.
- `UiScreen` içinde somut sınıf referansı olmaması, tema değişimini düşük maliyetli yapar.
- Temaya özel tüm ürünlerin birlikte değiştirilmesi gereken durumlarda bakım maliyeti düşer.

## Ne zaman kullanılır?
- Ürünler aile halinde birlikte değişiyorsa.
- Çoklu tema/platform/tenant varyantları varsa.
- Tutarsız ürün kombinasyonlarını compile-time’a yakın yerde engellemek istiyorsan.

## Ne zaman kullanma?
- Tek ürün var ve aile ilişkisi yoksa.
- Varyant sayısı çok düşük ve stabilse.

## Artılar / Eksiler

**Artılar**
- Aile tutarlılığı
- Client tarafında düşük bağımlılık
- Varyant geçişinde temiz mimari

**Eksiler**
- Yeni ürün türü eklendiğinde tüm factory ailesi etkilenir
- Başlangıçta soyutlama maliyeti vardır

## Kısa özet
Abstract Factory, özellikle UI ve platform farklılaşmasının yoğun olduğu projelerde “doğru kombinasyonla üretim” garantisi vererek sistem bütünlüğünü korur.
