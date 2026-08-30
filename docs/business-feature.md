# قابلیت تجاری دوم: محدودیت ظرفیت و اعتبارسنجی سبد

## مقایسه دو گزینه PDF

### گزینه A: قوانین تخفیف قابل ترکیب

کوچک‌ترین نسخه قابل قبول این گزینه هم نیاز دارد ترتیب ruleها، سقف تخفیف و جلوگیری از اعمال دوباره مشخص شود. تخفیف دسته‌بندی‌شده به اطلاعاتی مانند category نیاز دارد که مدل فعلی اصلاً نگه نمی‌دارد. بنابراین قبل از رسیدن به رفتار تجاری، باید abstraction جدید برای rule، priority و شاید مدل Item وارد شود. این گزینه قابل تست است، ولی برای اندازه فعلی پروژه complexity زیادی ایجاد می‌کند.

### گزینه B: validation و محدودیت سبد

این گزینه مستقیماً روی `addItem` و Map موجود قابل پیاده‌سازی است. مرز ظرفیت، duplicate در حالت پر، آزاد شدن ظرفیت بعد از remove، خطاهای ورودی و atomicity، سناریوهای رفتاری معناداری می‌سازند و به dependency یا معماری جدید نیاز ندارند.

## پیشنهاد و تأیید

گزینه B پیشنهاد شد، چون آزمایش TDD قوی‌تری با تغییر معماری کمتر ایجاد می‌کند. قرارداد زیر در checkpoint ارائه شد و کاربر با پیام «continue» ادامه آن را تأیید کرد.

## قرارداد رفتاری

- سبد حداکثر ۱۰ نام کالای متمایز نگه می‌دارد.
- افزودن کالاهای متمایز اول تا دهم مجاز است.
- افزودن نام متمایز یازدهم `IllegalStateException` ایجاد می‌کند.
- افزودن دوباره نام موجود حتی در ظرفیت کامل مجاز است؛ قیمت همان entry جایگزین می‌شود و count ثابت می‌ماند.
- حذف یک کالا یک slot آزاد می‌کند و پس از آن افزودن نام جدید مجاز است.
- `addItem` برای نام `null`، خالی یا blank، `IllegalArgumentException` ایجاد می‌کند.
- قیمت صفر، منفی، `NaN` یا بی‌نهایت نیز `IllegalArgumentException` ایجاد می‌کند.
- validation ورودی قبل از بررسی ظرفیت انجام می‌شود؛ بنابراین ورودی نامعتبر در سبد پر هم خطای validation می‌دهد.
- هر شکست باید atomic باشد: count، total و discount هیچ تغییری نمی‌کنند.
- ظرفیت بر اساس نام‌های متمایز Map است، نه تعداد دفعات فراخوانی `addItem`.

متن دقیق پیام exception جزو قرارداد تست نیست؛ نوع exception و وضعیت قابل مشاهده سبد معیار اصلی هستند.

## برنامه TDD

ابتدا تست‌های مرز ۱۰/۱۱، duplicate در ظرفیت کامل، remove و آزاد شدن slot، نام/قیمت نامعتبر، اولویت validation و atomicity نوشته می‌شوند. سپس حداقل پیاده‌سازی در Green اضافه می‌شود. اگر validation بین `addItem` و `updateItemPrice` تکرار شود، فقط بعد از Green به helper مشترک منتقل خواهد شد.

## Red

در commit `ae90afd` تعداد ۱۰ test method نوشته شد که با ورودی‌های پارامتریک ۱۶ execution ایجاد می‌کنند.

```bash
mvn --batch-mode --no-transfer-progress \
  -Dtest=ShoppingCartConstraintsTest test
```

```text
Tests run: 16, Failures: 13, Errors: 0, Skipped: 0
Expected IllegalArgumentException/IllegalStateException, but nothing was thrown
BUILD FAILURE
```

سه تست در Red پاس شدند، چون Map اولیه از قبل ده item، جایگزینی duplicate و remove سپس add را از نظر مکانیکی انجام می‌داد. سیزده failure باقی‌مانده ثابت کردند policy ظرفیت و validation هنوز وجود ندارد.

## Green

در commit `b73a13c` ثابت ظرفیت ۱۰ و guardهای مستقیم زیر به `addItem` اضافه شدند:

- validation نام؛
- validation قیمت؛
- رد نام جدید در حالت ظرفیت کامل؛
- اجازه جایگزینی نام موجود با `containsKey`.

```text
Focused feature suite: 16 passed
Complete suite: 40 passed, 0 failed, 0 errors, 0 skipped
BUILD SUCCESS
```

پیاده‌سازی قبل از `put` همه خطاها را بررسی می‌کند؛ بنابراین failure هیچ تغییر جزئی در Map ایجاد نمی‌کند.

## Refactor

بعد از Green، validation نام و قیمت بین `addItem` و `updateItemPrice` تکراری بود. در commit `005fa55` این منطق به `validateItemName` و `validatePrice` منتقل شد. پارامتر description پیام‌های قبلی `price` و `new price` را نیز حفظ کرد.

```text
Focused add/update suites: 34 passed
Complete suite after refactor: 40 passed
BUILD SUCCESS
```

## نتیجه کیفیت پس از قابلیت

```text
ShoppingCart Line Coverage: 33/34 (97.06%)
ShoppingCart Branch Coverage: 17/18 (94.44%)
ShoppingCart Method Coverage: 9/9 (100%)
PIT: 21 mutations, 20 killed, 0 survived, 1 no coverage
Mutation score: 95.24%
```

PIT هر چهار کلاس تست ShoppingCart را وارد تحلیل کرد. تنها mutant بدون پوشش همچنان return مسیر `removeItem` برای نام ناموجود است که در Task 5 بررسی خواهد شد.
