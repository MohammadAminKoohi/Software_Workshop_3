# تحلیل نهایی کیفیت تست‌ها

## وضعیت ابتدای Task 5

پیش از اضافه کردن تست جدید، suite شامل ۴۰ تست بود و همگی پاس می‌شدند.

```text
ShoppingCart Line Coverage: 33/34 (97.06%)
ShoppingCart Branch Coverage: 17/18 (94.44%)
ShoppingCart Method Coverage: 9/9 (100%)
PIT: 21 mutants, 20 killed, 0 survived, 1 no coverage
Mutation score: 95.24%
```

تنها مورد گزارش‌شده PIT در خط ۲۴ متد `removeItem` بود:

```text
replaced boolean return with true for ShoppingCart::removeItem
status: NO_COVERAGE
```

این mutant معادل نبود. اگر حذف نام ناموجود به‌جای `false` مقدار `true` بدهد، caller تصور می‌کند state تغییر کرده است. بنابراین نوشتن تست برای آن ارزش رفتاری داشت و صرفاً برای بالا بردن score نبود.

## تست‌های اضافه‌شده

فایل `ShoppingCartAdvancedScenariosTest` در commit `ee02067` اضافه شد و ۱۳ execution دارد:

- سبد خالی؛
- حذف نام ناموجود و ثابت ماندن state؛
- حذف دوباره یک کالا؛
- چند بار خواندن تخفیف بدون compound شدن یا تغییر total پایه؛
- مقادیر ۹۹٫۹۹، ۱۰۰ و ۱۰۰٫۰۱ در اطراف مرز تخفیف؛
- توالی add → update → discount؛
- توالی add → remove → update بدون زنده شدن دوباره کالا؛
- چرخه duplicate → update → remove؛
- اعشار در update و remove؛
- قیمت `Double.MAX_VALUE` و محدود ماندن نتیجه تخفیف؛
- ناموفق بودن remove در سبد پر بدون آزاد کردن capacity.

Mockito استفاده نشد، چون `ShoppingCart` هیچ dependency خارجی یا collaborator قابل جداسازی ندارد. Mock کردن خود کلاس فقط تست implementation می‌ساخت و fault detection را بهتر نمی‌کرد.

## نتیجه نهایی JaCoCo

فرمان:

```bash
mvn --batch-mode --no-transfer-progress clean verify
```

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

پوشش کل پروژه از ShoppingCart کمتر است، چون `Main` نمونه IDE و کلاس استفاده‌نشده `Item` همچنان اجرا نمی‌شوند. اضافه کردن تست مصنوعی برای آن‌ها به هدف رفتاری این آزمایش کمک نمی‌کرد.

## نتیجه نهایی PIT

فرمان:

```bash
mvn --batch-mode --no-transfer-progress test-compile \
  org.pitest:pitest-maven:mutationCoverage
```

| معیار | Baseline | نهایی |
|---|---:|---:|
| Total mutants | 11 | 21 |
| Killed | 9 | 21 |
| Survived | 0 | 0 |
| No coverage | 2 | 0 |
| Mutation score | 81.82% | 100% |
| Test strength روی mutantهای پوشش‌داده‌شده | 100% | 100% |

در اجرای نهایی survivor یا no-coverage باقی نماند. تست `removingMissingItemReturnsFalseWithoutChangingCart` همان mutant مهم Task 5 را کشت و تست‌های قبلی نیز بقیه تغییرات PIT را تشخیص دادند.

## چرا Coverage بالا کافی نیست؟

Coverage فقط می‌گوید یک خط یا branch اجرا شده است؛ تضمین نمی‌کند assertion نتیجه مهم آن را بررسی کرده باشد. مثال روشن همین پروژه است: قبل از Task 5، Line Coverage کلاس 97.06% بود، اما مسیر `return false` حذف نام ناموجود اصلاً اجرا نشده بود. حتی اگر یک تست مسیر را اجرا کند ولی مقدار بازگشتی را assert نکند، ممکن است Coverage کامل شود و mutant همچنان زنده بماند.

Mutation Testing سؤال قوی‌تری می‌پرسد: اگر رفتار کد عمداً کمی خراب شود، تست متوجه می‌شود؟ رسیدن هم‌زمان به coverage کامل کلاس و کشتن ۲۱/۲۱ mutant نشان می‌دهد suite فعلی علاوه بر اجرا، روی خروجی‌های کلیدی assertion دارد. با این حال، این score فقط برای مجموعه mutatorهای فعال PIT است و اثبات ریاضی نبود همه باگ‌های ممکن نیست.

## تصمیم درباره mutantها

یک mutant مهم no-coverage در ابتدای Task وجود داشت و تست رفتاری مناسب برای آن اضافه شد. در نتیجه نهایی هیچ survivor مهم، equivalent mutant یا مورد مبهمی باقی نماند و نیازی به تست قراردادی/مصنوعی یا checkpoint تصمیم‌گیری نبود.
