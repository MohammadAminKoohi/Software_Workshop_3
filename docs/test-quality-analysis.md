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

فایل `ShoppingCartAdvancedScenariosTest` در commit `ee02067` اضافه شد. این فایل ۱۱ test method دارد و تست پارامتری مرز تخفیف با سه ورودی اجرا می‌شود؛ بنابراین در مجموع ۱۳ execution ایجاد می‌کند. هیچ تستی فقط برای بالا بردن عدد Coverage نوشته نشد و هر مورد یک رفتار قابل مشاهده را محافظت می‌کند:

| تست | چرا نوشته شد؟ | رفتار یا خطایی که بررسی می‌کند |
|---|---|---|
| `emptyCartHasZeroState` | حالت پایه سبد در تست‌های قبلی به‌صورت کامل بررسی نشده بود. | سبد تازه باید count صفر و total و discounted total برابر صفر داشته باشد. |
| `removingMissingItemReturnsFalseWithoutChangingCart` | PIT مسیر حذف نام ناموجود را بدون پوشش گزارش کرده و mutant تغییر `false` به `true` را ساخته بود. | حذف نام ناموجود باید `false` برگرداند و count، total و discount را تغییر ندهد. |
| `removingSameItemTwiceReportsSecondRemovalAsMissing` | یک حذف موفق به‌تنهایی رفتار فراخوانی تکراری را مشخص نمی‌کرد. | حذف اول موفق و حذف دوم ناموفق است و پس از آن سبد خالی باقی می‌ماند. |
| `readingDiscountRepeatedlyDoesNotCompoundOrChangeBaseTotal` | ممکن بود getter تخفیف state را تغییر دهد یا هر بار تخفیف دیگری روی نتیجه قبلی اعمال کند. | چند بار خواندن discount باید نتیجه یکسان بدهد و total پایه و count را دست‌نخورده نگه دارد. |
| `discountBehaviorAroundThreshold` | خطای باگ اول دقیقاً در boundary رخ داده بود و فقط بررسی یک طرف مرز کافی نبود. | سه مقدار ۹۹٫۹۹، ۱۰۰ و ۱۰۰٫۰۱ نشان می‌دهند تخفیف فقط برای مقدار بیشتر از ۱۰۰ اعمال می‌شود. |
| `addUpdateAndDiscountSequenceUsesLatestState` | تست‌های واحد جداگانه تضمین نمی‌کردند چند عملیات متوالی از state جدید استفاده کنند. | بعد از add و update، total و discount باید از آخرین قیمت‌ها محاسبه شوند و عبور معکوس از مرز تخفیف درست باشد. |
| `removedItemIsNotRecreatedByUpdate` | قرارداد update نام ناموجود no-op بود و باید بعد از یک remove واقعی نیز حفظ می‌شد. | update روی کالای حذف‌شده نباید آن را دوباره بسازد یا state را تغییر دهد. |
| `duplicateAddUpdateAndRemoveFollowSingleEntryLifecycle` | استفاده از Map برای duplicate یک قرارداد رفتاری مهم ایجاد می‌کند که در یک چرخه کامل بررسی نشده بود. | add تکراری، update و remove باید همگی روی همان entry کار کنند و count هیچ‌گاه به دو نرسد. |
| `decimalValuesRemainStableAcrossUpdateAndRemoval` | دو باگ اعشاری قبلی ممکن بود پس از ترکیب update و remove دوباره ظاهر شوند. | جمع ۰٫۱ و ۰٫۲ و state باقی‌مانده پس از حذف باید بدون خطای دودویی قابل مشاهده محاسبه شوند. |
| `largeFinitePriceKeepsFiniteDiscount` | تست‌های معمول رفتار نزدیک بیشینه `double` را نشان نمی‌دادند. | `Double.MAX_VALUE` باید به‌عنوان قیمت محدود پذیرفته شود و تخفیف آن نیز محدود و کمتر از total باقی بماند. |
| `failedRemovalDoesNotFreeCapacity` | بررسی جداگانه remove و capacity تضمین نمی‌کرد failure ظرفیت را به اشتباه تغییر ندهد. | حذف ناموجود در سبد پر نباید slot آزاد کند و افزودن نام یازدهم همچنان باید exception بدهد. |

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
