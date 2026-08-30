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

