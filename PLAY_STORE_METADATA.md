# Store listing — тексты и функции приложения

Тексты ниже описывают текущую версию приложения и синхронизированы с Fastlane metadata. Поля и расчёты указаны по реализации Android-приложения.

## Русский

**Название:** Личные финансы & FIRE аудит

**Краткое описание:** Личные финансы: бюджет, капитал, финансовый аудит и моделирование FIRE.

**Полное описание:**

«Личные финансы & FIRE аудит» помогает записать доходы, расходы, активы и долги, увидеть финансовые показатели и смоделировать возможный срок достижения FIRE (финансовой независимости и раннего выхода на пенсию).

Приложение содержит четыре экрана:

• **Данные:** месячные доходы и расходы; ликвидные, финансовые и имущественные активы; обязательства; ожидаемая доходность, инфляция и налог на прирост капитала; дата рождения и возраст дожития. Категории сопровождаются подсказками. Можно выбрать символ одной из 13 валют; конвертации и курсов валют нет.
• **Показатели:** денежный поток, норма сбережений, резерв и его запас в месяцах, коэффициент долга, чистый капитал, ориентир накоплений Fidelity, коэффициент финансовой свободы и реальная доходность.
• **Оценка и аудит:** общий балл финансового здоровья от 0 до 10, оценки отдельных показателей, текстовый разбор сильных сторон и рисков. Итог и заключение можно передать через системное меню Android.
• **FIRE:** срок до расчётной финансовой независимости, целевой капитал, возраст выхода и график накопления и расходования капитала до заданного возраста дожития. Ползунки позволяют отдельно менять сбережения/доход, расходы и реальную доходность. Доступны 14 положительных и стрессовых сценариев.

FIRE-модель использует постоянную реальную доходность и приведённую стоимость расходов на оставшийся срок планирования. Правило 4% и Trinity Study не применяются. Стартовый капитал включает ликвидные и финансовые активы; имущество не включается, а долг уменьшает капитал только в части, превышающей стоимость имущества. Сценарии могут менять временные и будущие расходы и добавлять разовые поступления; в расчёте также может учитываться оценка налога на прирост финансовых активов.

Сценарии симулятора: рост дохода на 20%; рост финансовых активов на 25%; снижение расходов на 15% или 30%; наследство в размере 12 месячных доходов; продажа имущества с поступлением до 20 месячных доходов; снижение расходов на 25% через 18 лет; разовый резерв на потерю работы в размере 12 месячных доходов; падение финансовых активов на 30%; ипотечные платежи 35% дохода в течение 20 лет; автоплатежи 20% дохода в течение 5 лет; разовая трата в размере 5 месячных доходов; увеличение расходов на 25% (сценарий назван инфляцией); дополнительные расходы на ребёнка или иждивенца в размере 25% на 18 лет.

Интерфейс доступен на русском и английском языках. Приложение работает без разрешения на интернет: данные и расчёты остаются на устройстве. Выбор валюты меняет только отображаемый символ, суммы автоматически не конвертируются.

Все расчёты и сценарии носят информационный характер и не являются финансовой, инвестиционной или налоговой рекомендацией. Результаты зависят от введённых данных и упрощённых предположений модели.

**Ключевые слова:** личные финансы, бюджет, аудит финансов, финансовый калькулятор, FIRE, финансовая независимость, пенсия, чистый капитал, активы, долги, денежный поток, норма сбережений, подушка безопасности, финансовая свобода, доходность, инфляция.

## English

**Title:** Personal finance and FIRE

**Short description:** Personal finance, capital audit, financial metrics and FIRE projections.

**Full description:**

Personal Finance & FIRE Audit helps you record income, expenses, assets and liabilities, review financial indicators, and model a possible timeline to FIRE (financial independence and early retirement).

The app has four screens:

• **Input Data:** monthly income and expenses; liquid, financial and property assets; liabilities; expected return, inflation and capital-gains tax assumption; date of birth and life-expectancy age. Expandable hints explain input categories. Choose from 13 currency symbols; amounts are not converted and exchange rates are not provided.
• **Metrics:** cash flow, savings rate, emergency reserve and months of coverage, debt ratio, net worth, Fidelity age-based savings benchmark, financial freedom ratio, and real return.
• **Score & Audit:** an overall financial-health score from 0 to 10, component ratings, and a written review of positive indicators and risks. Share the summary or audit opinion through Android's system share menu.
• **FIRE:** projected time to financial independence, target capital, retirement age, and a chart comparing projected and required capital through the selected life-expectancy horizon. Sliders adjust savings/income, expenses and real return in a separate what-if projection. Fourteen positive and stress-test scenarios are available.

The FIRE model applies a constant real return and estimates the present value of expenses over the remaining planning horizon. It does not use the 4% rule or Trinity Study. Starting FIRE capital includes liquid and financial assets; property is excluded, and liabilities reduce the base only to the extent they exceed property value. Scenarios can change temporary and future expenses and add one-off proceeds; the model can also include an estimated tax on gains from financial assets.

Scenarios include: income +20%; financial assets +25%; expenses −15% or −30%; a one-off inheritance of 12 monthly incomes; property-sale proceeds capped at 20 monthly incomes; expenses −25% after 18 years; a one-off reserve draw equal to 12 monthly incomes for job loss; financial assets −30%; mortgage payments of 35% of income for 20 years; car payments of 20% of income for 5 years; a one-off expense of 5 monthly incomes; expenses +25% (labeled as inflation in the app); and child/dependent costs of 25% of expenses for 18 years.

The interface is available in Russian and English. The app works without the internet permission; data and calculations remain on-device. Currency selection changes the displayed symbol only and does not convert amounts.

All calculations and scenarios are informational and are not financial, investment or tax advice. Results depend on the entered data and the model's simplified assumptions.

**Keywords:** personal finance, budgeting, financial audit, FIRE calculator, financial independence, retirement planning, net worth, assets, liabilities, cash flow, savings rate, emergency fund, financial freedom, investment return, inflation.

## Store settings

- **Category:** Finance
- **Tags:** Personal Finance, Financial Planning, Calculator
- **Contains ads:** No
- **Age audience:** 18+
