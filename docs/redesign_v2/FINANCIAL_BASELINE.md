# Финансовый эталон v2 (ARCH-01)

Дата фиксации: 21.11.2026. Контролируемая дата для всех расчётов: `2026-10-31`.

## 1. Проверенные функции

- `FinanceCalculator.calculate`, `calculateAge`, `calculateFidelityMultiplier`, `calculateScores`, `calculateTotalScore`, `generateTrajectory`.
- `ProjectionEngine`: `findYearsToRetirement`, `accumulatedCapitalAt`, `requiredCapitalAt`, `computeBaseRequirement`, `taxInfo`, `buildTrajectory`, `ProjectionParams.from`, `ProjectionParams.fireStartCapital`.
- `FinancialMetricEvaluator`: cash flow, savings rate, safety months, debt ratio, net worth, fidelity, CFS, real return, total score, пороги и статусы.
- `SimulatorScaleCalculator`: `calculateSavingsConfig`, `calculateExpensesConfig`, `calculateReturnConfig`, `calculateNiceStep`.
- `ScenarioConstants`: `calculateScenarioAmounts`, `calculateSimulationOngoingExpenses`.
- `AuditorAnalysisGenerator`: группировка positive/neutral/negative и экспорт (покрытие через существующие тесты).
- Модели: `FinancialInputData`, `CalculatedMetrics`, `FinancialScores`, `FireStatus`, `TrajectoryPoint`.

## 2. Детерминированные тесты

Новый тест: `app/src/test/java/com/example/FinancialBaselineTest.kt`.

Точные входы и выходы:

### Денежный поток и норма сбережений

- Вход: `monthlyIncome=6000`, `monthlyExpenses=4800`, валюта `$`, дата `2026-10-31`.
- Выход: `cashFlow=1200.00`, `savingsRate=20.00%`.
- Статусы: cash flow `POSITIVE`, savings rate `NEUTRAL` (граница `<= 20%`).
- Нулевой доход: `monthlyIncome=0`, `monthlyExpenses=4800` → `cashFlow=-4800.00`, `savingsRate=-100.00%`, оба статуса `NEGATIVE`.

### Капитал и риски

- Вход: US average defaults, `birthDate=1988-10-31`, `lifeExpectancy=82`, доходность 7.5%, инфляция 2.5%, налог 20%.
- Выход: `fidelityCapital=55000.00`, `fidelityTarget=187200.00`, `netWorth=155000.00`, `CFS=2.690972`, `totalScore=6.7`, статус общего балла `NEUTRAL`.
- `fidelityCapital(15000, 40000, 280000, 180000)=55000`.
- `fidelityCapital(15000, 40000, 280000, 300000)=35000`.

### Пороги позитивного/нейтрального/негативного

- Cash flow: `0` → `NEGATIVE`, `0.01` → `POSITIVE`.
- Savings rate: `0` → `NEGATIVE`, `20` → `NEUTRAL`, `20.01` → `POSITIVE`.
- Total score: `3.4` → `NEGATIVE`, `3.5` → `NEUTRAL`, `7.0` → `POSITIVE`.
- CFS: `1.99` → `NEGATIVE`, `2.0` → `NEUTRAL`, `5.0` → `POSITIVE`.
- Net worth: `50/100` → `POSITIVE` (включительная граница 50%), `49.99/100` → `NEUTRAL`.

### FIRE

- Обычный будущий FIRE: US average defaults, дата `2026-10-31`.
  - `status=REACHABLE`, `yearsToRetirement=24.00`, `retirementAge=62.00`, `targetCapital=803009.58`.
- Достигнутый FIRE: доход 100000, расходы 50000, активы по 100000000, возраст 36, доходность 7%, инфляция 2%.
  - `status=REACHED_NOW`, `yearsToRetirement=0.00`, `targetCapital=11427014.47`.
- Недостижимый FIRE: доход 30000, расходы 100000, активы 10000, фин. активы 0, долги 200000, возраст 36, доходность 2%, инфляция 10%.
  - `status=UNREACHABLE`, `yearsToRetirement=44.00` (максимальный горизонт).
- Невалидный возраст: `birthDate=invalid-date`.
  - `status=INVALID_AGE`, `yearsToRetirement=0.00`, `retirementAge=0.00`, `targetCapital=0.00`, траектория пуста.

### Сценарии симуляции

- `calculateScenarioAmounts(6000, 120000, false)`:
  - `monthlyIncome=6000`, `salaryIncrease=1200`, `inheritance=72000`, `propertySale=120000`, `jobLoss=72000`, `mortgageMonthly=2100`, `carLoanMonthly=1200`, `largeOneOffExpense=30000`.
- `calculateSimulationOngoingExpenses(60000, 10000, optimization=true, relocation=false, inflation=false)`:
  - `beforeReduction=51000`, `effective=41000`.

### Общий балл

- `calculateTotalScore(10, 10, 10, 4)=6.0`.
- `calculateTotalScore(10, 10, 10, 5) > 6.0` (cap снимается при CFS-компоненте >= 5).

## 3. Текущие ограничения

- Денежные значения хранятся в `Double`; сравнения с нулём возможны только после округления до валютной точности.
- `FinanceCalculator.calculate` использует `LocalDate.now()` по умолчанию; тесты обязаны передавать контролируемую дату.
- FIRE-расчёт использует бисекцию и округление вверх до 0.01 года; допуски сравнения — 0.01.
- При `financialAssets == 0` налог на прирост капитала принудительно равен 0.
- При `monthlyIncome <= 0` норма сбережений становится `-100%` при отрицательном потоке и `0%` иначе.
- `ProjectionEngine` зависит от `isAgeValid`; при невалидном возрасте результат `INVALID_AGE` и пустая траектория.
- Существующие тесты покрывают 14 сценариев и их комбинации в `ScenarioSimulationTest`; ARCH-01 не добавляет дублирующих E2E-проверок, а фиксирует детерминированные константы и базовые метрики.

## 4. Подозрительные результаты

Подозрительных результатов не обнаружено. Все значения соответствуют текущей семантике кода и замороженным решениям AGENTS.md.
