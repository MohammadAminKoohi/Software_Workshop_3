# تعاملات معنادار با Codex

## Interaction 1

### Context

پیش از هر تغییر باید دو پوشه توسعه‌دهنده و جداسازی هویت Git/GitHub بررسی می‌شد.

### Request

بررسی مسیر مطلق، ریشه Git، شاخه، وضعیت، تنظیمات محلی نام/ایمیل، remote، کلید SSH و حساب GitHub هر پوشه بدون clone مجدد.

### Codex response summary

Codex هر دو مخزن را بررسی کرد و نشان داد هر دو از هویت global توسعه‌دهنده A و host عمومی `github.com` استفاده می‌کنند؛ در نتیجه پوشه توسعه‌دهنده B نیز به حساب A متصل بود. همچنین یک تغییر whitespace در تست اولیه پیدا شد.

### Our technical critique

تشخیص صرف نام owner در remote برای تعیین هویت کافی نبود. اعتبارسنجی `ssh -T` برای aliasهای اختصاصی شواهد قوی‌تری فراهم کرد. تغییر تست نیز باید پیش از شروع پاک می‌شد.

### Decision

برای هر مخزن `user.name` و `user.email` محلی و SSH host alias اختصاصی تنظیم شد و تغییر whitespace تست اولیه بازگردانده شد.

### Accepted / rejected / modified

Accepted: جداسازی محلی و alias اختصاصی. Rejected: تغییر مکرر تنظیمات global.

### Result

هر دو worktree پاک شدند؛ Developer A با `github-dev-a` و Developer B با `github-dev-b` احراز هویت می‌شوند.

## Interaction 2

### Context

GitHub MCP اولیه `Auth: Unsupported` و فهرست ابزار خالی داشت و `gh` تازه نصب شده بود.

### Request

فعال‌سازی دو instance مستقل GitHub MCP برای `MohammadAminKoohi` و `arshiaizd`.

### Codex response summary

Codex مستندات رسمی MCP را بررسی کرد، هر دو حساب را با device flow در `gh` احراز هویت کرد، و دو wrapper ساخت که token هر username را از macOS keychain می‌خوانند و به container رسمی GitHub MCP می‌دهند.

### Our technical critique

OAuth موقت داخل container در restart نیاز به ورود دوباره داشت. pin کردن حساب با `gh auth token --user` جداسازی پایدارتر ایجاد کرد، بدون ذخیره token در فایل.

### Decision

دو server با نام‌های `github-dev-a` و `github-dev-b` و toolsetهای محدود به نیاز پروژه ساخته شد.

### Accepted / rejected / modified

Modified: OAuth موقت با wrapper مبتنی بر keychain جایگزین شد. Rejected: token ثابت در TOML یا script.

### Result

هر instance با ۵۰ ابزار و هویت صحیح تست شد؛ Issues، PR، review، merge، Projects v2، Actions و branchها در دسترس هستند.

## Interaction 3

### Context

پیش از Task 1 باید PDF کامل خوانده و GitHub Project/Issues ایجاد می‌شد.

### Request

PDF را منبع حقیقت قرار بده، Hamgit را کنار بگذار، یک Kanban با پنج وضعیت دقیق و شش Issue مرتب و تقسیم‌شده بین دو توسعه‌دهنده ایجاد کن.

### Codex response summary

Codex هر شش صفحه PDF را استخراج و خواند، Project خالی موجود را برای جلوگیری از duplicate دوباره استفاده کرد، وجود پنج status دقیق را تأیید نمود، شش Issue را ساخت و ۳/۳ تخصیص داد.

### Our technical critique

استفاده مجدد از Project موجود بهتر از ایجاد Project دوم بود. title پروژه با ابزار MCP قابل تغییر نبود و باید در UI نام‌گذاری شود.

### Decision

Issue #1 در Ready و Issueهای #2 تا #6 در Backlog قرار گرفتند؛ هیچ label غیرضروری ساخته نشد.

### Accepted / rejected / modified

Accepted: GitHub-only workflow و استفاده مجدد از Project. Rejected: ایجاد board تکراری یا Hamgit remote.

### Result

برد و شش Issue با assignment صحیح آماده شدند و سپس Issue #1 در شروع Task 1 به In progress منتقل شد.

## Interaction 4

### Context

پروژه اولیه build file نداشت، ولی باید پیش از تغییر source/test اجرا و baseline Coverage/Mutation واقعی ثبت می‌شد.

### Request

برای شش Task playbook مرحله‌به‌مرحله ایجاد کن و سپس Task 1 را بدون تغییر تست‌های اولیه ادامه بده.

### Codex response summary

Codex شش فایل Markdown اجرایی در ریشه workspace ساخت، پروژه دست‌نخورده را با POM موقت خارج مخزن اجرا کرد، سپس Maven/JaCoCo/PIT و workflowهای CI را فقط به‌عنوان زیرساخت افزود.

### Our technical critique

ساخت مستقیم POM در مخزن پیش از اولین اجرا، شواهد «untouched run» را ضعیف می‌کرد. runner موقت این مشکل را حل کرد. معیارهای `ShoppingCart` باید جدا از `Main` و `Item` گزارش شوند تا کد نمونه IDE نتیجه را مخدوش نکند.

### Decision

Java 17 به‌عنوان target قابل بازتولید، Maven به‌دلیل نصب بودن و layout ساده، JaCoCo 0.8.15 و PIT 1.30.0 انتخاب شدند.

### Accepted / rejected / modified

Accepted: runner موقت و گزارش کلاس/کل پروژه. Rejected: افزودن تست جدید برای بالا بردن baseline.

### Result

۴ تست از ۴ تست پاس شدند؛ `ShoppingCart` به Line 84.21%، Branch 66.67%، Method 85.71% و Mutation Score 81.82% رسید.

## Interaction 5

### Context

در شروع Task 2، متن PDF و تست اولیه درباره مرز تخفیف با هم تناقض داشتند.

### Request

رفتار واقعی edge caseها بررسی شود، اما در صورت ابهام یا تعارض قبل از تغییر کار متوقف شود.

### Codex response summary

Codex با یک اجرای آزمایشی نشان داد مجموع دقیق ۱۰۰ به ۹۰ تبدیل می‌شود. سپس مشخص کرد PDF صریحاً نبود تخفیف در این مرز را می‌خواهد، در حالی که تست `testDiscountAtBoundary_WRONG` انتظار ۹۰ دارد و تغییر تست‌های اولیه نیز ممنوع شده است.

### Our technical critique

اصلاح مستقیم کد بدون حل تناقض باعث شکست دائمی suite می‌شد. دور زدن تست یا شرط‌گذاری بر اساس تعداد کالا نیز پیاده‌سازی غیرواقعی و مغایر قرارداد بود.

### Decision

مسئله در checkpoint مطرح شد. کاربر اجازه داد assertion اشتباه اصلاح شود و خواست این تغییر به‌صورت شفاف مستند گردد.

### Accepted / rejected / modified

Accepted: اصلاح test contract با اجازه صریح و سپس تغییر حداقلی شرط. Rejected: غیرفعال کردن تست یا نوشتن کد خاص برای پاس کردن دو انتظار متناقض.

### Result

Red واقعی مقدار ۹۰ را در برابر انتظار ۱۰۰ ثبت کرد؛ پس از تغییر `>=` به `>`، تست متمرکز و هر چهار تست کامل پاس شدند.

## Interaction 6

### Context

بعد از اصلاح مرز تخفیف، یکی از ورودی‌های آزمایشی نشان داد جمع ۰٫۱ و ۰٫۲ دقیقاً با literal مورد انتظار برابر نیست.

### Request

یک باگ واقعی و مستقل مربوط به دقت اعشاری با Red/Green اثبات شود، بدون آنکه صرفاً برای Coverage تست اضافه شود.

### Codex response summary

Codex تست رفتاری `0.1 + 0.2 = 0.3` را پیشنهاد و اجرا کرد. تست با خروجی `0.30000000000000004` شکست خورد و سپس جمع داخلی با `BigDecimal.valueOf` پیاده‌سازی شد.

### Our technical critique

استفاده از delta بزرگ در assertion فقط خطا را پنهان می‌کرد. از طرف دیگر، round کردن به تعداد رقم ثابت قراردادی بود که PDF تعیین نکرده است. جمع دهدهی بدون scale اجباری، هدف رفتاری را دقیق‌تر حفظ می‌کند.

### Decision

API مبتنی بر `double` حفظ شد، ولی عملیات جمع داخلی با `BigDecimal` انجام گرفت و در انتها نتیجه به `double` برگشت.

### Accepted / rejected / modified

Accepted: decimal accumulation. Rejected: assertion با tolerance سهل‌گیرانه و rounding اجباری. Modified: به جای تغییر نوع Map، فقط کوچک‌ترین بخش محاسبه مجموع تغییر کرد.

### Result

Red مقدار واقعی خطادار را ثبت کرد و پس از Green، تست متمرکز و مجموعه کامل ۵ تستی پاس شدند.

## Interaction 7

### Context

بعد از رفع جمع اعشاری، لازم بود بررسی شود آیا خود فرمول تخفیف هنوز خطای مستقلی دارد یا نه.

### Request

یک ورودی واقعی پیدا شود که جمع را درگیر نکند ولی محاسبه ده درصد تخفیف را آشکار کند.

### Codex response summary

Codex ورودی‌های اعشاری بالاتر از ۱۰۰ را با نتیجه دهدهی مقایسه کرد و قیمت ۱۰۰٫۲۲ را یافت. سبد تک‌قلمی مقدار `90.19800000000001` برمی‌گرداند، در حالی که نتیجه دهدهی ۹۰٫۱۹۸ است.

### Our technical critique

استفاده از همان ورودی ۰٫۱ و ۰٫۲، استقلال این باگ از جمع را ثابت نمی‌کرد. ورودی تک‌قلمی ۱۰۰٫۲۲ نشان می‌دهد مشکل در عمل ضرب مسیر تخفیف است.

### Decision

یک تست جدا برای تخفیف اضافه شد و فقط ضرب `total * 0.9` با ضرب دهدهی جایگزین شد.

### Accepted / rejected / modified

Accepted: ورودی تک‌قلمی و اصلاح موضعی. Rejected: یکی دانستن دو خطای عددی یا تعریف abstraction اضافه برای یک ضریب ثابت.

### Result

Red اختلاف دقیق را ثبت کرد؛ Green تست متمرکز و suite شش‌تستی را پاس کرد. سه چرخه مستقل Task 2 اکنون کامل هستند.

## Interaction 8

### Context

پس از تکمیل سه باگ، اجرای PIT فقط یک کلاس تست را گزارش کرد، با اینکه پروژه دو کلاس تست داشت.

### Request

پیش از PR بررسی شود که CI، Coverage و Mutation Testing واقعاً تست‌های جدید را اجرا می‌کنند.

### Codex response summary

Codex تنظیم `targetTests` را بررسی کرد و دید الگوی `ShoppingCartTest*` با `ShoppingCartHiddenBugsTest` match نمی‌شود. الگو به `ShoppingCart*Test*` تغییر کرد و PIT دوباره اجرا شد.

### Our technical critique

موفق بودن فرمان PIT به‌تنهایی کافی نبود؛ تعداد کلاس‌های کشف‌شده نشان می‌داد گزارش ناقص است. حذف کامل filter هم ممکن بود در آینده تست‌های نامرتبط را وارد این تحلیل کند.

### Decision

filter حفظ، اما به‌اندازه لازم گسترده شد تا همه کلاس‌های تست ShoppingCart را پوشش دهد.

### Accepted / rejected / modified

Accepted: اصلاح محدود pattern. Rejected: اعتماد صرف به exit code یا حذف کامل target filter.

### Result

PIT دو کلاس تست را کشف کرد و با ۹ mutant، تعداد ۸ killed، صفر survived و یک no-coverage با موفقیت پایان یافت.

## Interaction 9

### Context

پیش از تست‌نویسی برای `updateItemPrice`، PDF چند ورودی مبهم را مطرح کرده بود و stub موجود نیز امضای `int` داشت.

### Request

قرارداد کامل متد، شامل رفتار نام ناموجود، ورودی نامعتبر، duplicate، اعشار، مقدار بزرگ و اثر خطا بر وضعیت، قبل از Red برای تأیید ارائه شود.

### Codex response summary

Codex از امضای PDF، مثال‌های کامنت‌شده و ساختار Map یک قرارداد پیشنهادی ساخت: update برای نام موجود، no-op برای نام ناموجود معتبر، exception برای نام blank و قیمت غیرمثبت/غیرمحدود، validation پیش از lookup و حفظ کامل وضعیت در شکست.

### Our technical critique

رفتار no-op برای نام ناموجود صرفاً حدس نبود و با تست نمونه پروژه پشتیبانی می‌شد. در مقابل، پذیرش قیمت صفر با عبارت PDF درباره ورودی نامعتبر سازگار نبود. وابستگی تست‌ها به متن دقیق exception نیز شکننده تشخیص داده شد.

### Decision

کاربر همه بندهای قرارداد را تأیید کرد. نام‌ها دقیق و case-sensitive باقی می‌مانند، duplicateها طبق Map یک entry دارند و API مورد نیاز از `double` استفاده می‌کند.

### Accepted / rejected / modified

Accepted: قرارداد پیشنهادی و حداقل ۱۰ تست پیش از implementation. Rejected: ساخت کالای جدید در update، تغییر return type، rounding دلخواه و assertion روی متن کامل خطا.

### Result

قرارداد پیش از هر تغییر test/production در `docs/update-item-price.md` ثبت شد و مبنای فاز Red قرار گرفت.

## Interaction 10

### Context

قرارداد `updateItemPrice` تأیید شده بود و باید حداقل هشت تست معنادار پیش از implementation نوشته می‌شد.

### Request

تست‌ها همه رفتارهای تأییدشده را پوشش دهند، Red واقعی ثبت شود و Green فقط حداقل کد لازم باشد.

### Codex response summary

Codex پانزده test method طراحی کرد که با ورودی‌های پارامتریک ۱۸ بار اجرا می‌شوند. Red به دلیل نبود امضای `double` با ۱۵ خطای compilation ثبت شد. سپس validation و `Map.replace` به‌عنوان پیاده‌سازی حداقلی اضافه شدند.

### Our technical critique

تعداد تست به‌تنهایی هدف نبود؛ تست‌های جدا برای تغییر state، تخفیف، خطا و atomicity لازم بودند. استفاده از `containsKey` و سپس `put` دو lookup انجام می‌داد، در حالی که `replace` دقیقاً قرارداد no-op را فراهم می‌کند. Refactor مصنوعی بعد از Green نیز ارزش فنی نداشت.

### Decision

Red compilation failure مطابق PDF پذیرفته شد، چون API خواسته‌شده هنوز وجود نداشت. Green با دو guard و یک `replace` تکمیل شد و مرحله Refactor به‌صورت مستند «لازم نبود» باقی ماند.

### Accepted / rejected / modified

Accepted: تست‌های سناریومحور، parameterization برای گروه قیمت‌های نامعتبر و `Map.replace`. Rejected: تست‌های تکراری صرفاً برای بالا بردن count، assertion متن کامل exception و refactor بدون فایده.

### Result

۱۸ execution متمرکز و کل suite شامل ۲۴ تست پاس شدند. Coverage کلاس به Line 96.15%، Branch 92.86% و Method 100% رسید و PIT سیزده mutant از چهارده mutant را کشت.

## Interaction 11

### Context

برای قابلیت تجاری دوم، PDF دو مسیر ruleهای تخفیف ترکیبی و validation/constraint را پیشنهاد می‌کرد.

### Request

هر دو گزینه از نظر ارزش TDD و پیچیدگی معماری مقایسه شوند و پیش از implementation یک گزینه دقیق برای تأیید ارائه شود.

### Codex response summary

Codex توضیح داد که composable discountها در مدل فعلی به rule abstraction، ترتیب اجرا، category و جلوگیری از double-discount نیاز دارند. در مقابل، محدودیت ظرفیت ده نام متمایز همراه با validation ورودی، boundary و atomicity را روی ساختار Map فعلی قابل تست می‌کند.

### Our technical critique

صرف اضافه کردن validation مشابه Task 3 قابلیت تجاری کافی نبود. اضافه شدن مرز ظرفیت، رفتار duplicate در حالت full و remove سپس add، سناریوهای stateful و مستقل ایجاد می‌کند. عدد ظرفیت و نوع خطا نیز باید قبل از تست ثابت می‌شدند.

### Decision

گزینه B با ظرفیت ۱۰ نام متمایز، `IllegalStateException` برای overflow، `IllegalArgumentException` برای ورودی نامعتبر و حفظ atomicity پیشنهاد شد. کاربر با دستور «continue» اجرای همین پیشنهاد را تأیید کرد.

### Accepted / rejected / modified

Accepted: constraint محدود و قابل مشاهده روی Map فعلی. Rejected: rule engine، category و priority بدون نیاز واقعی. Modified: validation تنها به‌عنوان بخشی از policy کامل ظرفیت پذیرفته شد، نه یک قابلیت مستقل کم‌عمق.

### Result

قرارداد و مقایسه پیش از تغییر test/production در `docs/business-feature.md` ثبت شد و Task 4 وارد فاز Red شد.

## Interaction 12

### Context

قابلیت ظرفیت/validation تأیید شده بود و باید با سناریوهای valid، invalid، boundary، state و error وارد چرخه کامل TDD می‌شد.

### Request

Red واقعی برای ظرفیت ۱۰، overflow، duplicate، remove، ورودی نامعتبر، اولویت خطا و atomicity ساخته و سپس کمترین Green و Refactor موجه اجرا شود.

### Codex response summary

Codex ده test method با ۱۶ execution نوشت. Red سیزده failure ثبت کرد. Green با ثابت ظرفیت و guardهای مستقیم تکمیل شد و سپس duplication واقعی validation بین add/update در commit جداگانه به دو helper منتقل شد.

### Our technical critique

پاس شدن سه تست در Red مشکل نبود، چون feature suite همچنان سیزده failure رفتاری داشت. برای Green استخراج فوری helper انجام نشد تا حداقل پیاده‌سازی از cleanup جدا بماند. در Refactor نیز پیام‌های قبلی exception حفظ شدند.

### Decision

Green ابتدا با validation تکراری اما واضح commit شد؛ بعد از عبور کامل suite، helperهای مشترک در Refactor جداگانه ساخته شدند.

### Accepted / rejected / modified

Accepted: تست‌های پارامتریک برای گروه ورودی نامعتبر، state assertion شامل total/discount و Refactor واقعی duplication. Rejected: rule engine، mock و مخلوط کردن cleanup با Green.

### Result

کل suite شامل ۴۰ تست پاس شد. Coverage کلاس به Line 97.06%، Branch 94.44% و Method 100% رسید؛ PIT بیست mutant از بیست‌ویک mutant را کشت.
