# توسعه updateItemPrice با TDD

## قرارداد تأییدشده

قرارداد زیر پیش از نوشتن تست یا تغییر کد production در checkpoint مطرح و توسط کاربر تأیید شد:

- امضای متد `void updateItemPrice(String itemName, double newPrice)` است.
- اگر نام دقیق کالا وجود داشته باشد، فقط قیمت همان entry تغییر می‌کند.
- تعداد کالاها با update تغییر نمی‌کند و مجموع و تخفیف باید بر اساس قیمت جدید محاسبه شوند.
- نام کالای ناموجود با ورودی‌های معتبر یک no-op است؛ این رفتار با مثال کامنت‌شده پروژه پایه سازگار است.
- نام `null`، خالی یا فقط شامل فاصله با `IllegalArgumentException` رد می‌شود.
- قیمت صفر، منفی، `NaN` یا بی‌نهایت با `IllegalArgumentException` رد می‌شود.
- اعتبار ورودی‌ها پیش از بررسی وجود کالا انجام می‌شود و هر خطا باید بدون تغییر وضعیت سبد باشد.
- نام‌ها case-sensitive هستند و trim یا normalize نمی‌شوند.
- رفتار نام تکراری همان قرارداد فعلی Map است: آخرین `addItem` مقدار entry واحد را تعیین می‌کند و update همان entry را تغییر می‌دهد.
- قیمت اعشاری مثبت و قیمت محدود بزرگ پذیرفته می‌شوند.

پیام‌های خطا برای فهم بهتر علت شکست در پیاده‌سازی قرار می‌گیرند، ولی تست‌ها به متن دقیق پیام وابسته نمی‌شوند؛ نوع خطا و ثابت ماندن وضعیت رفتار اصلی هستند.

## مواردی که انتخاب نشدند

- ساخت entry جدید برای نام ناموجود، چون update نباید ساختار سبد را تغییر دهد.
- برگرداندن `boolean`، چون امضای خواسته‌شده در PDF از نوع `void` است.
- exception برای نام ناموجود، چون مثال موجود پروژه no-op را نشان می‌دهد.
- مجاز دانستن قیمت صفر یا منفی، چون PDF آن‌ها را در گروه ورودی‌های نامعتبر این قابلیت قرار داده است.
- گرد کردن قیمت به تعداد رقم ثابت، چون scale پولی مشخصی در صورت مسئله تعریف نشده است.

## برنامه تست Red

پیش از پیاده‌سازی، تست‌های جدا برای این رفتارها نوشته می‌شوند: تغییر مجموع، ثابت ماندن تعداد، عبور تخفیف به بالا و پایین مرز، no-op نام ناموجود، نام‌های نامعتبر، قیمت‌های نامعتبر، اولویت validation، نام تکراری، حساسیت به حروف، قیمت اعشاری و مقدار محدود بزرگ.

فاز Red، خروجی Green و commitهای واقعی در ادامه همین فایل ثبت خواهند شد.

## Red

در commit `9cb313c` فایل `ShoppingCartUpdateItemPriceTest` با ۱۵ test method نوشته شد. دو تست پارامتریک در مجموع ۱۸ execution ایجاد می‌کنند. چون stub اولیه پارامتر `int` داشت ولی قرارداد PDF از `double` استفاده می‌کند، اولین اجرا در مرحله test compilation شکست خورد:

```bash
mvn --batch-mode --no-transfer-progress \
  -Dtest=ShoppingCartUpdateItemPriceTest test
```

```text
Compiling 3 test source files
15 errors: incompatible types: possible lossy conversion from double to int
BUILD FAILURE
```

این شکست پیش از هر تغییر production ثبت و commit شد. علاوه بر نبود امضای درست، بدنه stub نیز خالی بود و هیچ‌کدام از رفتارهای success، validation یا atomicity را فراهم نمی‌کرد.

## Green

در commit `9361767` امضا به `double` تغییر کرد. validation نام و قیمت در ابتدای متد قرار گرفت و `items.replace(name, newPrice)` حداقل عملیات لازم را انجام داد. `replace` برای نام موجود مقدار را عوض می‌کند و برای نام ناموجود بدون ساخت entry جدید no-op است.

```text
Focused update-price suite: 18 passed
Complete suite: 24 passed, 0 failed, 0 errors, 0 skipped
BUILD SUCCESS
```

تست‌ها بالا و پایین رفتن از مرز تخفیف، مجموع، تعداد، نبود اثر جانبی، duplicate، case sensitivity، اعشار و `Double.MAX_VALUE` را نیز بررسی کردند.

## Refactor

Refactor جداگانه انجام نشد. استفاده مستقیم از `Map.replace` هم lookup و هم update را در یک عملیات خوانا انجام می‌دهد. اضافه کردن helper یا abstraction دیگر برای این متد کوتاه، فقط کد را بیشتر می‌کرد.

## ارزیابی پس از Green

```text
ShoppingCart Line Coverage: 25/26 (96.15%)
ShoppingCart Branch Coverage: 13/14 (92.86%)
ShoppingCart Method Coverage: 7/7 (100%)
PIT: 14 mutations, 13 killed, 0 survived, 1 no coverage
Mutation score: 92.86%
```

PIT هر سه کلاس تست ShoppingCart را پیدا کرد. mutant بدون پوشش همچنان مسیر حذف کالای ناموجود است و از محدوده رفتار `updateItemPrice` خارج است.
