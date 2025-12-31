# Project Flow — Restaurant Management System

This file summarizes the full app: user flows, screens, and key functionality. It is based on source packages under `app/src/main/java/com/dineout/code` and project files (Firebase config, Gradle). Use this as a quick reference for features and navigation.

1) Customer / Ordering Flow
- Entry screens
  - `WelcomePage` (order package): App entry for customers; chooses to view menu or continue as guest.
  - `MainActivity` (order package): Customer main screen showing categories or menu overview.
- Browsing & selecting
  - `Menu`, `MenuItem`, `Item`: Menu models and list screens displaying dishes and details.
  - Tap item → details with `setQuantity` screen/modal; `quantityInvalidPrompt` handles invalid inputs.
- Cart & checkout
  - `cart`, `cartListView`: Cart UI listing selected items, quantities, per-item totals.
  - `confirmOrder`: Order confirmation screen showing final items, totals, table/tablet selection if applicable.
  - `Order`, `OrderDetails`: Order model and detail views for order summary and status.
- Notifications & time
  - `NotificationClass`: Local notification helper to inform users about order status.
  - `Time`: Utility used for timestamps and scheduling display.

Outcome: Customer places order which is pushed to Firebase (or app backend) and becomes visible to kitchen/hall.

2) Kitchen Flow
- Entry & monitoring
  - `MainActivity` (kitchen package): Kitchen dashboard listing incoming orders.
  - `OrderViewHolder`, adapters (RecyclerViewAdapterOrdersOfCook/etc.): UI for listing orders and dishes per cook.
- Cook management
  - `CooksListActivity`, `RecyclerViewAdapterCookList`, `RecyclerViewAdapterCook`: Manage/list cooks and assign orders.
  - `AttendanceActivity`, `RecyclerViewAdapterAttendanceList`: Track cook attendance.
- Order processing
  - Kitchen staff mark order items as prepared/ready; events propagate to hall/ready queues.
  - `listener/` package: event listeners for order updates (Firebase listeners or local events).

Outcome: Orders move from 'placed' → 'preparing' → 'ready', triggering hall notifications.

3) Hall (Waiter / Front-of-House) Flow
- Setup & management
  - `TableSetup`, `Tables`, `Tablets`: Screens to configure tables and associate tablets/devices.
  - `Tablets` and `Tablet` model (admin package) manage tablet registrations.
- Order reception & serving
  - `ReadytoServeOrders`: List of orders ready for pickup by waiters.
  - `ViewCompletedOrders`: View orders completed and served.
  - `ReceivePayment`: Screen to accept payments at table or mark as paid.
- Utilities & interaction
  - `OnSwipeTouchListener`: Gesture helpers for list interactions (swipe to mark/assign).
  - `Tracking`: Order tracking utilities for hall staff.
  - `FireBaseHelper` (hall/DB): Abstraction for reading/writing orders/tables to Firebase.

Outcome: Hall staff deliver ready orders, update statuses and optionally trigger billing flow.

4) Billing Flow
- Bill generation & confirmation
  - `OrderBill`: Build bill view from order items, tax, discounts.
  - `BillAdapter`, `OrdersAdapter`: UI adapters to show items/previous orders within billing screens.
  - `ConfirmPayment`: Screen to finalize payment method and record transaction.
  - `PendingPayments`: View/list of unpaid or pending bills.
- Payment models
  - `DishOrder`, `DishPrice`: Models for line items and pricing used in bill calculations.
  - `Feedback`: Optional feedback screen after payment.

Outcome: Payment recorded, receipts optionally emailed or stored; order marked closed.

5) Admin / Management Flow
- Authentication & dashboard
  - `LoginActivity` (admin): Admin authentication screen.
  - `AdminPanelActivity`, `HomeActivity`: Admin dashboard with quick actions.
- CRUD operations
  - Add/update entities: `AddEmployeeActivity`, `AddItem`, `AddMenuItemActivity`, `AddTableActivity`, `AddTabletActivity`, `Updateitem`.
  - Models: `Employee`, `Menu`, `MenuItem`, `Item`, `Table`, `Tablet` for persisted app data.
- Inventory & ingredients
  - `IngredientsList`, `IngredientsListAdapter`, `IngredientRow`: View and manage ingredient stocks.
  - `Inventory` (order package): Inventory model used during ordering/reservation.
- Notifications
  - `Notifications`, `NotificationsAdapter`, `NotificationClass`: Admin notification center for alerts and messages.
- Reporting & schedules
  - `EndOfWeekActivitiy`: Admin view for weekly summaries and actions.

Outcome: Admins manage menus, staff, devices, inventory, and config for the restaurant.

6) Reporting & Email Utilities
- End-of-day / end-of-week processing
  - `EndOfDay_EventHandler`: Handles scheduled end-of-day tasks and report generation.
- Emailing reports
  - `GMailSender`, `EmailSender/`: Utilities to send emails (reports, alerts) via SMTP.
  - `JSSEProvider`: Security provider used for secure mail connections.
- Reporting models
  - `order` (reporting package): Specialized order model used for reports and summaries.

Outcome: Automatic reports/emails for managers and stakeholders.

7) Cross-cutting and Infrastructure
- Firebase integration
  - `google-services.json` in `app/`: Firebase configuration enabling Realtime DB and/or Auth.
  - `FireBaseHelper` (hall) and other Firebase usages across packages for syncing orders, user data, and device registrations.
- Notification & email
  - `NotificationClass` appears in multiple packages to centralize notifications.
- Security & networking
  - `JSSEProvider` for secure sockets; email utilities use SMTP with TLS.
- Database backup
  - `database/Firebase Realtime DB Backup.json` contains a snapshot/backup of production or test DB.

8) Key screens mapping (quick reference)
- Customer: `WelcomePage`, `MainActivity` (order), `Menu` list, `MenuItem` → `setQuantity` → `cart` → `confirmOrder`.
- Kitchen: `MainActivity` (kitchen), `CooksListActivity`, order list adapters.
- Hall: `TableSetup`, `ReadytoServeOrders`, `ViewCompletedOrders`, `ReceivePayment`.
- Billing: `OrderBill`, `ConfirmPayment`, `PendingPayments`.
- Admin: `LoginActivity`, `AdminPanelActivity`, `Add*` screens, `IngredientsList`.
- Reporting: `EndOfDay_EventHandler`, `GMailSender` utilities.

9) Recommendations / Next steps
- Expand this document with class-level responsibilities: list each class, primary public methods, and Intent actions used between activities.
- Add sequence diagrams for these flows (Customer→Kitchen→Hall→Billing) to visualize status transitions.
- Create a mapping of Firebase paths/keys used by each module (helps audit DB structure).

File generated by repository scan. For expansions, tell me which flow or package to document in deeper detail.
