# تحلیل خط پایه پروژه Shopping Cart

تاریخ اندازه‌گیری: ۳۰ اوت ۲۰۲۶  
شاخه: `baseline/tooling`  
نسخه اولیه: `5658ddbbff70f6eab3b35d2cd5ef6c8dfc036219`

## حفاظت از پروژه اولیه

- هش blob تست اولیه `Test/ShoppingCartTest.java`: `661e5e2cae9ef018fd61f944ea09245f7be38706`
- هیچ‌یک از فایل‌های `src/` یا `Test/` در Task 1 تغییر نکرده‌اند.
- اجرای اولیه با یک POM موقت در `/tmp` انجام شد تا پیش از ثبت ابزار ساخت، مخزن دست‌نخورده بماند.
- خروجی ساخت نیز در `/tmp/swe-lab3-untouched-target` قرار گرفت.

## محیط و ساخت اولیه

| مورد | نتیجه |
|---|---|
| سیستم ساخت اولیه | فاقد Maven/Gradle؛ فقط IntelliJ module |
| Java نصب‌شده محلی | OpenJDK 23.0.1 |
| نسخه هدف قابل بازتولید | Java 17 |
| Maven | 3.9.9 |
| Gradle | نصب نیست |
| فایل CI اولیه | وجود نداشت |
| JaCoCo اولیه | پیکربندی نشده بود |
| PIT اولیه | پیکربندی نشده بود |

فرمان اجرای دست‌نخورده:

```bash
mvn -f /tmp/swe-lab3-untouched-runner-pom.xml clean test
```

نتیجه واقعی:

```text
Compiling 3 source files with javac [release 17]
Compiling 1 test source file
Tests run: 4, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
Git status: clean on baseline/tooling
Starter test blob: 661e5e2cae9ef018fd61f944ea09245f7be38706
```

## ساختار و مسئولیت‌ها

### `ShoppingCart`

- کالاها را در `HashMap<String, Double>` نگه می‌دارد.
- `addItem` مقدار قیمت را بر اساس نام در Map قرار می‌دهد؛ نام تکراری مقدار قبلی را جایگزین می‌کند.
- `removeItem` برای نام موجود حذف و `true`، و برای نام ناموجود `false` برمی‌گرداند.
- `getTotal` قیمت‌های Map را با `double` جمع می‌کند.
- `getTotalWithDiscount` در پیاده‌سازی اولیه برای مجموع `>= 100` ضریب `0.9` اعمال می‌کند.
- `getItemCount` اندازه Map را برمی‌گرداند.
- `updateItemPrice(String, int)` وجود دارد ولی بدنه آن خالی است؛ امضای لازم صورت مسئله `double` است.

### کلاس‌های دیگر

- `Item` یک value object تغییرناپذیر برای نام و قیمت است، اما `ShoppingCart` از آن استفاده نمی‌کند.
- `Main` فقط کد نمونه تولیدشده توسط IDE است و در رفتار سبد خرید نقشی ندارد.

## تست‌های اولیه فعال

| تست | رفتار پوشش‌داده‌شده |
|---|---|
| `testAddItem` | افزودن یک قلم، تعداد ۱ و مجموع ۵۰ |
| `testRemoveItem` | حذف قلم موجود، مقدار بازگشتی `true`، تعداد و مجموع صفر |
| `testDiscountAtBoundary_WRONG` | انتظار تخفیف برای مجموع دقیقاً ۱۰۰ |
| `testDiscountAboveThreshold` | تخفیف ۱۰٪ برای مجموع ۱۲۰ |

سه تست `updateItemPrice` در فایل اولیه کامنت شده‌اند و در شمارش ۴ تست اجراشده نیستند. تست مرز ۱۰۰ با نام `WRONG` و انتظار ۹۰ با قرارداد PDF که تخفیف را فقط برای مجموع بیشتر از ۱۰۰ می‌خواهد ناسازگار است؛ اصلاح این رفتار باید در Task مربوط به باگ‌ها با Red/Green واقعی انجام شود، نه در تحلیل خط پایه.

## شکاف‌های رفتاری و فرضیه‌ها

موارد زیر در این مرحله فقط شکاف یا فرضیه‌اند و تا زمان تست آشکارکننده، «باگ اثبات‌شده» محسوب نمی‌شوند:

- مسیر حذف ناموجود و حذف چندباره؛
- مسیر بدون تخفیف در `getTotalWithDiscount`؛
- مرز دقیق ۱۰۰ مطابق قرارداد PDF؛
- نام `null` یا خالی؛
- قیمت صفر، منفی، بی‌نهایت یا `NaN`؛
- قیمت‌های اعشاری حساس و مقادیر بسیار بزرگ؛
- رفتار نام‌های تکراری و جایگزینی در Map؛
- اثر جانبی شکست در حذف یا به‌روزرسانی؛
- امضا و رفتار واقعی `updateItemPrice`.

## Coverage خط پایه

فرمان:

```bash
mvn --batch-mode --no-transfer-progress clean verify
```

گزارش‌ها در `target/site/jacoco/` تولید شدند.

### کلاس `ShoppingCart`

| معیار | پوشش | درصد |
|---|---:|---:|
| Line | 16 / 19 | 84.21% |
| Branch | 4 / 6 | 66.67% |
| Method | 6 / 7 | 85.71% |

### کل کد تولید

| معیار | پوشش | درصد |
|---|---:|---:|
| Line | 16 / 30 | 53.33% |
| Branch | 4 / 8 | 50.00% |
| Method | 6 / 12 | 50.00% |

پایین‌تر بودن پوشش کل عمدتاً ناشی از اجرا نشدن `Item` و `Main` است. معیار اصلی آزمایش برای تحلیل رفتاری، کلاس `ShoppingCart` است؛ هر دو نمای کلاس و کل پروژه برای شفافیت ثبت شده‌اند.

## Mutation Testing خط پایه

فرمان:

```bash
mvn --batch-mode --no-transfer-progress test-compile \
  org.pitest:pitest-maven:mutationCoverage
```

گزارش‌ها در `target/pit-reports/` تولید شدند.

| معیار | نتیجه |
|---|---:|
| کل mutantها | 11 |
| Killed | 9 |
| Survived | 0 |
| No coverage | 2 |
| Mutation score | 81.82% (9 / 11) |
| Test strength روی mutantهای پوشش‌داده‌شده | 100% (9 / 9) |

### mutantهای بدون پوشش

1. جایگزینی مقدار بازگشتی مسیر بدون تخفیف در خط ۳۲؛ هیچ تست فعالی مجموع کمتر از ۱۰۰ را از `getTotalWithDiscount` بررسی نمی‌کند.
2. جایگزینی مقدار بازگشتی مسیر حذف ناموجود در خط ۱۷؛ هیچ تست فعالی `removeItem` برای نام غایب را اجرا نمی‌کند.

افزودن تست برای این شکاف‌ها به Taskهای TDD بعدی موکول شده است تا خط پایه دست‌کاری نشود و تاریخچه Red/Green واقعی باقی بماند.

## ابزار قابل بازتولید افزوده‌شده

- Maven با layout اولیه `src/` و `Test/`؛
- JaCoCo 0.8.15 برای گزارش HTML/XML/CSV؛
- PIT 1.30.0 و افزونه JUnit 5 نسخه 1.2.3؛
- GitHub Actions برای build/test/JaCoCo روی PR و `main`؛
- workflow دستی برای PIT.

