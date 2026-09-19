# آموزش جامع React برای توسعه‌دهندگان Kotlin (پروژه هتل زاگرس)

این فایل به عنوان دفترچه یادگیری تو عمل می‌کند. از سطح مقدماتی تا حرفه‌ای (Senior) را با مقایسه با دانش کاتلین تو پیش می‌بریم.

---

## بخش ۱: از کاتلین به ری‌اکت (The Mental Model)

اگر با **Jetpack Compose** کار کرده باشی، ری‌اکت برایت بسیار آشنا خواهد بود.

### ۱. کامپوننت چیست؟
در کاتلین ما `@Composable` داریم، در ری‌اکت **Functional Component**.

| مفهوم | در Kotlin (Compose) | در React (TypeScript) |
| :--- | :--- | :--- |
| **تعریف تابع** | `fun MyButton(text: String)` | `const MyButton = ({ text }: Props) => { ... }` |
| **حالت (State)** | `val count by remember { mutableStateOf(0) }` | `const [count, setCount] = useState(0);` |
| **ورودی‌ها** | Parameters | Props |
| **تغییر UI** | Recomposition | Re-rendering |

### ۲. بررسی فایل `Checkbox.tsx`
کدی که داری می‌نویسی در واقع یک بلوک UI مستقل است:

```tsx
// تعریف نوع داده‌های ورودی (مثل Data Class در کاتلین)
interface CheckboxProps {
  label: string;
  checked: boolean;
  onChange: (checked: boolean) => void;
  disabled?: boolean; // علامت سوال یعنی Optional
}

// تعریف کامپوننت
export const Checkbox: React.FC<CheckboxProps> = ({ label, checked, onChange, disabled }) => {
  return (
    <label className={`m3-checkbox ${disabled ? 'm3-checkbox--disabled' : ''}`}>
      <input
        type="checkbox"
        checked={checked}
        onChange={(e) => onChange(e.target.checked)} // Event Listener
        disabled={disabled}
      />
      <span className="m3-checkbox__label">{label}</span>
    </label>
  );
};
```

---

## بخش ۲: مدیریت وضعیت (State Management) - سطح Junior+

در کاتلین تو از `MutableState` استفاده می‌کنی. در ری‌اکت، هر بار که `setCount` (یا هر Setter دیگری) صدا زده شود، کل تابع دوباره اجرا می‌شود تا UI بروز شود.

**نکته سینیور:** برخلاف کاتلین که متغیرها مستقیماً تغییر می‌کنند، در ری‌اکت Stateها **Immutable** هستند. تو نباید مقدار را مستقیماً عوض کنی، بلکه همیشه باید از تابع `set` استفاده کنی.

---

## بخش ۳: چرخه حیات (Lifecycle) و Side Effects

در اندروید ما `LaunchedEffect` داریم. در ری‌اکت معادل آن **`useEffect`** است.

مثلاً برای گرفتن لیست هتل‌ها از سرور:
```tsx
useEffect(() => {
  // این کد مشابه init در ViewModel یا LaunchedEffect در Compose است
  fetchHotels().then(data => setHotels(data));
}, []); // آرایه خالی یعنی فقط یک‌بار زمان لود شدن اجرا شو
```

---

## نقشه راه پیش رو (Roadmap)

1.  **سطح ۱ (مقدماتی):** درک Props، State و رندر کردن لیست‌ها (مثل LazyColumn در اندروید).
2.  **سطح ۲ (متوسط):** کار با فرم‌ها، ارتباط با API (فایل‌های `HotelRepository` تو) و Navigation.
3.  **سطح ۳ (حرفه‌ای):** مدیریت وضعیت پیشرفته (Context API یا Zustand)، بهینه‌سازی رندرها (Memoization) و نوشتن تست‌های واحد.

---

## بخش ۴: معماری حرفه‌ای (Bridging Kotlin to React)

یکی از جذاب‌ترین بخش‌های پروژه تو، نحوه اتصال منطق کاتلین به UI ری‌اکت است. فایل `useReservationViewModel.ts` یک الگوی **Observer Pattern** عالی را پیاده کرده است.

### مقایسه با ViewModel اندروید:
در اندروید، تو یک `StateFlow` در ViewModel داری که UI به آن گوش می‌دهد (`collectAsState`).

در ری‌اکت پروژه تو:
1.  **Kotlin Bridge:** منطق بیزنس (مثل لاگین و رزرو) در کاتلین نوشته شده و از طریق یک Bridge در دسترس است.
2.  **Hook (`useReservationViewModel`):** این هوک نقش واسط را دارد.
    *   `useState`: برای ذخیره آخرین وضعیتِ دریافت شده از کاتلین.
    *   `useEffect`: برای **Subscribe** کردن به تغییرات کاتلین. وقتی در کاتلین تغییری رخ دهد، تابع `setState` صدا زده شده و ری‌اکت UI را بروز می‌کند.
    *   **Cleanup:** در انتهای `useEffect` یک تابع `unsubscribe` برگردانده شده که مشابه `onCleared` در ViewModel عمل می‌کند تا از Memory Leak جلوگیری شود.

### چرا این کار سینیور است؟
چون "Logic" را از "UI" کاملاً جدا کرده است. اگر فردا بخواهی به جای ری‌اکت از یک فریم‌ورک دیگر استفاده کنی، تمام کدهای کاتلین (اعتبار‌سنجی‌ها، محاسبات قیمت و ...) دست‌نخورده باقی می‌مانند.

---

## تمرین شماره ۲ (چالش واقعی):
به فایل `App.tsx` نگاه کن. ری‌اکت چطور متوجه می‌شود که باید فرم لاگین را نشان دهد یا صفحه رزرو را؟
*   پاسخ در متغیر `state.isLoggedIn` نهفته است.
*   **تمرین:** سعی کن در `App.tsx` یک شرط جدید اضافه کنی که اگر وضعیت در حال "Loading" بود (مثلاً `state.isLoading`)، یک متن ساده "Loading..." نشان دهد. (باید اول ببینی در `ReservationStateJs` چنین فیلدی هست یا نه).
