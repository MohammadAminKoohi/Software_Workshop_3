# آزمایش TDD — سبد خرید

## ۱. معرفی آزمایش

در این آزمایش یک سبد خرید کوچک را مرحله‌به‌مرحله بررسی و توسعه دادیم. هدف فقط رسیدن به تعداد زیادی تست نبود؛ می‌خواستیم تاریخچه Git نشان دهد هر رفتار چگونه تحلیل شده، چه تستی آن را آشکار کرده و حداقل تغییر لازم برای درست شدنش چه بوده است.

کار در شش Issue و روی branchهای جدا انجام شد. هر Task از مسیر Pull Request، اجرای CI و بازبینی خودکار مشخص عبور کرده است. توسعه بین دو حساب `MohammadAminKoohi` و `arshiaizd` تقسیم شد. قانون review/merge با حساب دیگر از Task 2 به بعد رعایت شد؛ PR اول پیش از مطرح شدن این قانون توسط همان حساب نویسنده merge شده بود و این مورد را به‌عنوان استثنا پنهان نکرده‌ایم.

## ۲. ساختار پروژه پایه

پروژه اولیه build file نداشت و layout آن نیز layout استاندارد Maven نبود:

```text
src/
├── Item.java
├── Main.java
└── ShoppingCart.java

Test/
└── ShoppingCartTest.java
```

کلاس `ShoppingCart` کالاها را در `HashMap<String, Double>` نگه می‌داشت. نام تکراری در این مدل یک entry جدید نمی‌سازد و قیمت قبلی را جایگزین می‌کند. `Item` در منطق سبد استفاده نشده و `Main` فقط کد نمونه IDE است.

برای قابل تکرار شدن اجرا، Maven با Java 17 اضافه شد. مسیرهای `src/` و `Test/` در `pom.xml` به Maven معرفی شدند و JaCoCo، PIT و GitHub Actions نیز روی همین ساختار تنظیم شدند.

## ۳. تحلیل Baseline

اولین اجرا با یک POM موقت بیرون repository انجام شد تا قبل از ثبت ابزار ساخت هیچ فایل پروژه تغییر نکند:

```bash
mvn -f /tmp/swe-lab3-untouched-runner-pom.xml clean test
```

نتیجه اولیه:

```text
Tests run: 4, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

تست‌های اولیه افزودن یک کالا، حذف کالای موجود و دو حالت تخفیف را پوشش می‌دادند. حذف نام ناموجود، مسیر بدون تخفیف، رفتار اعشاری، ورودی نامعتبر، duplicate و پیاده‌سازی واقعی `updateItemPrice` بدون پوشش مانده بودند.

شرح کامل این مرحله در [گزارش Baseline](docs/baseline-analysis.md) قرار دارد.

## ۴. Coverage و Mutation اولیه

نتیجه JaCoCo برای خود `ShoppingCart`:

| معیار | Baseline |
|---|---:|
| Line Coverage | 16/19 (84.21%) |
| Branch Coverage | 4/6 (66.67%) |
| Method Coverage | 6/7 (85.71%) |

نتیجه PIT:

```text
Total mutants: 11
Killed: 9
Survived: 0
No coverage: 2
Mutation score: 81.82%
```

دو mutant بدون پوشش مربوط به حذف کالای ناموجود و مسیر بدون تخفیف بودند. این نتیجه ثبت شد و برای بالا بردن مصنوعی baseline تست جدیدی اضافه نکردیم.

## ۵. باگ اول: تخفیف روی مجموع دقیق ۱۰۰

PDF می‌گوید تخفیف فقط برای مجموع **بیشتر از ۱۰۰** است. پیاده‌سازی شرط `>= 100` داشت و برای ورودی ۴۰ + ۶۰ مقدار ۹۰ برمی‌گرداند.

تست اولیه نیز به اشتباه همین ۹۰ را انتظار داشت و حتی نام آن `testDiscountAtBoundary_WRONG` بود. این تعارض در checkpoint مطرح شد و با اجازه صریح کاربر، همان assertion از ۹۰ به ۱۰۰ اصلاح شد. این تنها تغییر مجاز روی فایل تست اولیه است:

```text
hash اولیه: 661e5e2cae9ef018fd61f944ea09245f7be38706
hash بعد از اصلاح: a4742604ce5914f47ab5b98fdc4a95dbeb8aaa4b
```

- Red: `da3ac2d` — `expected: <100.0> but was: <90.0>`
- Green: `5991721` — تغییر `>=` به `>`
- Regression: چهار تست از چهار تست پاس شدند.

## ۶. باگ دوم: جمع قیمت‌های اعشاری

ورودی واقعی ۰٫۱ و ۰٫۲ این خطا را نشان داد:

```text
expected: <0.3> but was: <0.30000000000000004>
```

جمع مستقیم پول با `double` علت اصلی بود. بدون تغییر API عمومی، جمع داخلی با `BigDecimal.valueOf` انجام شد.

- Red: `50fcedf`
- Green: `56a9d4d`
- Regression: پنج تست از پنج تست پاس شدند.

## ۷. باگ سوم: محاسبه اعشاری تخفیف

بعد از درست شدن جمع، ضرب تخفیف هنوز خطای مستقلی داشت. یک کالای ۱۰۰٫۲۲ نتیجه زیر را می‌ساخت:

```text
expected: <90.198> but was: <90.19800000000001>
```

این تست فقط یک کالا داشت، پس failure از حلقه جمع نبود و دقیقاً عمل ضرب در `getTotalWithDiscount` را آشکار می‌کرد.

- Red: `cbfd271`
- Green: `e6bb97d` — محاسبه ضریب با `BigDecimal`
- Regression: شش تست از شش تست پاس شدند.

تحلیل کامل هر سه باگ، خروجی failure و دلیل دیده نشدن آن‌ها در تست‌های قبلی در [گزارش باگ‌های پنهان](docs/hidden-bugs.md) آمده است.

## ۸. توسعه updateItemPrice

قرارداد متد پیش از نوشتن تست تأیید شد:

```java
void updateItemPrice(String itemName, double newPrice)
```

نام موجود به‌روزرسانی می‌شود و count تغییر نمی‌کند. نام ناموجود معتبر no-op است. نام null/blank و قیمت صفر، منفی، `NaN` یا بی‌نهایت با `IllegalArgumentException` رد می‌شوند و failure هیچ اثر جانبی ندارد.

قبل از implementation تعداد ۱۵ test method نوشته شد که با parameterization در مجموع ۱۸ بار اجرا می‌شوند. Red به دلیل نبود امضای `double` با ۱۵ خطای compilation ثبت شد:

- قرارداد: `d166425`
- Red: `9cb313c`
- Green: `9361767` — دو validation guard و `Map.replace`
- Refactor: لازم نبود؛ `replace` از ابتدا کوتاه‌ترین عملیات مناسب بود.
- نتیجه کامل پس از Green: ۲۴/۲۴ تست پاس.

جزئیات قرارداد، سناریوها و خروجی‌ها در [گزارش updateItemPrice](docs/update-item-price.md) قرار دارد.

## ۹. قابلیت تجاری دوم

دو جهت پیشنهادی PDF مقایسه شدند:

- قوانین تخفیف ترکیبی به مدل category، ترتیب ruleها، priority و جلوگیری از double-discount نیاز داشت؛
- validation و محدودیت سبد با Map فعلی قابل پیاده‌سازی بود و boundary، state و atomicity بهتری برای این آزمایش ایجاد می‌کرد.

گزینه دوم تأیید شد. قرارداد نهایی، حداکثر ۱۰ نام متمایز است. نام یازدهم `IllegalStateException` می‌دهد، ولی جایگزینی نام موجود در سبد پر مجاز است. حذف کالا slot آزاد می‌کند و ورودی نامعتبر پیش از capacity check رد می‌شود.

- قرارداد و مقایسه: `7f94de4`
- Red: `ae90afd` — ۱۶ اجرا و ۱۳ failure
- Green: `b73a13c` — capacity و validation حداقلی
- Refactor: `005fa55` — استخراج validation مشترک add/update
- نتیجه کامل: ۴۰/۴۰ تست پاس.

شرح تصمیم و چرخه کامل در [گزارش قابلیت تجاری دوم](docs/business-feature.md) آمده است.

## ۱۰. تست‌های پیشرفته

در Task 5 به‌جای اضافه کردن تست بر اساس حدس، گزارش JaCoCo و XML خروجی PIT بررسی شد. یک mutant غیرمعادل باقی مانده بود که return حذف نام ناموجود را از `false` به `true` تبدیل می‌کرد.

در commit `ee02067` سیزده اجرای جدید برای این موارد اضافه شد:

- حذف ناموجود و حذف دوباره؛
- عدم compound شدن تخفیف؛
- مرزهای ۹۹٫۹۹، ۱۰۰ و ۱۰۰٫۰۱؛
- سناریوهای add → update → discount و add → remove → update؛
- duplicate، اعشار، قیمت بسیار بزرگ و capacity پس از حذف ناموفق.

Mockito استفاده نشد، چون کلاس هیچ dependency خارجی ندارد. گزارش کامل در [تحلیل کیفیت تست‌ها](docs/test-quality-analysis.md) قرار دارد.

## ۱۱. Coverage نهایی

### کلاس ShoppingCart

| معیار | Baseline | نهایی |
|---|---:|---:|
| Line Coverage | 16/19 (84.21%) | 34/34 (100%) |
| Branch Coverage | 4/6 (66.67%) | 18/18 (100%) |
| Method Coverage | 6/7 (85.71%) | 9/9 (100%) |

### کل کد production

| معیار | Baseline | نهایی |
|---|---:|---:|
| Line Coverage | 16/30 (53.33%) | 34/45 (75.56%) |
| Branch Coverage | 4/8 (50.00%) | 18/20 (90.00%) |
| Method Coverage | 6/12 (50.00%) | 9/14 (64.29%) |

عدد کل پروژه پایین‌تر است، چون `Main` نمونه IDE و `Item` استفاده‌نشده را فقط برای افزایش درصد تست نکردیم. معیار رفتاری اصلی این آزمایش `ShoppingCart` است.

## ۱۲. Mutation Testing نهایی

| معیار | Baseline | نهایی |
|---|---:|---:|
| Total mutants | 11 | 21 |
| Killed | 9 | 21 |
| Survived | 0 | 0 |
| No coverage | 2 | 0 |
| Mutation score | 81.82% | 100% |
| Test strength روی mutantهای پوشش‌داده‌شده | 100% | 100% |

در اجرای نهایی هیچ survivor یا no-coverage باقی نماند. این نتیجه فقط درباره mutatorهای فعال PIT است و به معنی نبود همه خطاهای ممکن نیست.

## ۱۳. مقایسه و برداشت نهایی از معیارها

پیش از Task 5، Line Coverage کلاس به 97.06% رسیده بود، اما PIT هنوز یک fault واقعی و بدون پوشش پیدا می‌کرد. بنابراین اجرای تقریباً همه خط‌ها به‌تنهایی ثابت نمی‌کرد که return valueهای مهم assert شده‌اند.

Coverage می‌پرسد «چه چیزی اجرا شد؟» ولی Mutation Testing می‌پرسد «اگر رفتار کمی خراب شود، تست متوجه می‌شود؟». تست حذف ناموجود هر دو معیار را بهتر کرد، چون هم branch جاافتاده را اجرا کرد و هم مقدار `false` را به‌صورت مستقیم سنجید.

## ۱۴. تعاملات مهم با Codex

تعداد ۱۴ تعامل واقعی در طول کار ثبت شده است؛ از audit هویت‌ها و راه‌اندازی دو MCP مستقل تا تحلیل باگ، تصمیم قرارداد، طراحی Red، ارزیابی Refactor، بررسی mutant و ممیزی نهایی گزارش.

هر ورودی شامل Context، Request، خلاصه پاسخ Codex، نقد فنی، تصمیم، موارد پذیرفته/رد/اصلاح‌شده و نتیجه است. فایل کامل: [تعاملات Codex](docs/codex-interactions.md).

## ۱۵. تاریخچه Git، Issueها و Pull Requestها

| Task | Issue | نویسنده | Pull Request | وضعیت |
|---|---|---|---|---|
| Baseline و ابزار تست | [#1](https://github.com/MohammadAminKoohi/Software_Workshop_3/issues/1) | Amin | [#7](https://github.com/MohammadAminKoohi/Software_Workshop_3/pull/7) | merged |
| سه باگ پنهان | [#2](https://github.com/MohammadAminKoohi/Software_Workshop_3/issues/2) | Arshia | [#8](https://github.com/MohammadAminKoohi/Software_Workshop_3/pull/8) | merged |
| updateItemPrice | [#3](https://github.com/MohammadAminKoohi/Software_Workshop_3/issues/3) | Amin | [#9](https://github.com/MohammadAminKoohi/Software_Workshop_3/pull/9) | merged |
| قابلیت تجاری دوم | [#4](https://github.com/MohammadAminKoohi/Software_Workshop_3/issues/4) | Arshia | [#10](https://github.com/MohammadAminKoohi/Software_Workshop_3/pull/10) | merged |
| کیفیت تست و Mutation | [#5](https://github.com/MohammadAminKoohi/Software_Workshop_3/issues/5) | Amin | [#11](https://github.com/MohammadAminKoohi/Software_Workshop_3/pull/11) | merged |
| گزارش و بازتولید | [#6](https://github.com/MohammadAminKoohi/Software_Workshop_3/issues/6) | Arshia | PR نهایی | in progress |

از squash استفاده نشد تا commitهای Red و Green در تاریخچه باقی بمانند. از PR #8 به بعد، نویسنده PR آن را merge نکرده است. PR #7 پیش از اضافه شدن این قاعده توسط حساب نویسنده merge شد. reviewهای خودکار با عنوان Automated Codex review ثبت شده‌اند و approval انسانی ساختگی ایجاد نشده است. وضعیت کارها در [GitHub Project #2](https://github.com/users/MohammadAminKoohi/projects/2) نگهداری می‌شود.

## ۱۶. نحوه اجرای پروژه

پیش‌نیازها:

- JDK 17 یا جدیدتر؛
- Maven 3.9 یا نسخه سازگار.

اجرای clean build، همه تست‌ها و JaCoCo:

```bash
mvn --batch-mode --no-transfer-progress clean verify
```

خروجی‌های مهم:

```text
target/surefire-reports/
target/site/jacoco/index.html
target/site/jacoco/jacoco.xml
target/site/jacoco/jacoco.csv
```

اجرای PIT:

```bash
mvn --batch-mode --no-transfer-progress test-compile \
  org.pitest:pitest-maven:mutationCoverage
```

گزارش PIT در `target/pit-reports/` ساخته می‌شود. workflow عادی GitHub Actions روی PR و `main` دستور `clean verify` را اجرا می‌کند و workflow جداگانه Mutation testing نیز به‌صورت دستی (`workflow_dispatch`) در دسترس است.

برای بازتولید از یک checkout تمیز:

```bash
git clone git@github.com:MohammadAminKoohi/Software_Workshop_3.git
cd Software_Workshop_3
mvn --batch-mode --no-transfer-progress clean verify
mvn --batch-mode --no-transfer-progress test-compile \
  org.pitest:pitest-maven:mutationCoverage
```

خروجی مورد انتظار نسخه نهایی:

```text
Tests run: 53, Failures: 0, Errors: 0, Skipped: 0
ShoppingCart: Line 100%, Branch 100%, Method 100%
PIT: 21/21 mutants killed, Mutation score 100%
```

## ۱۷. نتیجه‌گیری

پروژه از چهار تست اولیه و یک stub ناقص به ۵۳ تست سبز رسید. سه باگ با failure واقعی و commitهای Red/Green جدا اصلاح شدند، `updateItemPrice` بعد از تأیید قرارداد با ۱۸ اجرای پیش از implementation توسعه یافت، و قابلیت ظرفیت/validation چرخه کامل Red-Green-Refactor داشت.

مهم‌تر از درصد نهایی، مسیر رسیدن به آن است: تست‌ها بر اساس رفتار قابل مشاهده و گزارش mutant نوشته شدند، پیشنهادهای غیرضروری مانند mock یا rule engine رد شدند، و همه تصمیم‌های مبهم پیش از implementation در checkpoint ثبت شدند.
