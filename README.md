# Padmavathi General Stores — Website (v1)

Spring Boot + MySQL + vanilla JS website for a pickup-only general store: customers
browse products, build a cart, checkout with name/phone/pickup time, and the order
is saved to the database and also sent to the shop via a pre-filled WhatsApp message.

## What's included

- **Public storefront** (`/index.html`) — categories, product grid, cart drawer, checkout modal
- **Admin dashboard** (`/admin.html`) — view/update orders, add/delete categories & products (login required)
- **REST API** — see below
- **MySQL** via Spring Data JPA (tables auto-created from entities on first run)
- **Spring Security + JWT** — public browsing/ordering; product, category, upload and order-management APIs need an admin token from `/api/auth/login`

## Before you run it

1. Create a MySQL database:
   ```sql
   CREATE DATABASE padmavathi_store;
   ```

2. Open `src/main/resources/application.properties` and fill in:
   - `spring.datasource.username` / `spring.datasource.password` — your MySQL credentials
   - `shop.whatsapp-number` — your shop's WhatsApp number in international format, digits only
     (e.g. `919876543210` for a +91 number — no `+`, no spaces)

3. Run it:
   ```bash
   ./mvnw spring-boot:run
   ```
   (or open the project in your IDE — Eclipse/IntelliJ — and run `StoreApplication`)

4. Tables are created automatically the first time it runs (`spring.jpa.hibernate.ddl-auto=update`).

5. A default admin login is created automatically on first run:
   - **username:** `admin`
   - **password:** `Admin@123` (or whatever `app.admin.initial-password` is set to)

   Log in to `/admin.html` once and change this — either add a new admin user with
   a proper password (via a one-off script or a small "change password" endpoint
   you can add later) and delete the default one, or update the row directly in
   the `admin_user` table with a new BCrypt hash.

> **Deploying to Render?** See [DEPLOY.md](DEPLOY.md).

## Adding your first products

Once the app is running:
1. Go to `http://localhost:8080/admin.html`
2. Your browser will prompt for the admin login (username/password above)
3. Add a few categories first (e.g. "Groceries", "Dairy", "Snacks")
4. Add products under each category
5. Go to `http://localhost:8080/` to see them live in the storefront

## How ordering works

1. Customer adds products to cart (kept in the browser, not saved until checkout)
2. Checkout asks for Name, Phone, and Pickup time (ASAP / Today evening / Tomorrow morning)
3. On submit, the order + items are saved to the database and an order number is generated (e.g. `A1024`)
4. Customer is shown a "Send on WhatsApp" button with the full order pre-filled as a message
5. You confirm the order by calling the customer back
6. In the admin dashboard, update the order's status as you process it:
   `PENDING → CONFIRMED → READY_FOR_PICKUP → COMPLETED` (or `CANCELLED`)

## REST API reference

**Public**
- `GET /api/categories` — list all categories
- `GET /api/products` or `GET /api/products?category={id}` — list products
- `POST /api/orders` — place an order (body: customerName, phone, pickupTime, items[])

**Admin (requires login)**
- `GET /api/admin/orders` — all orders, newest first
- `PUT /api/admin/orders/{id}/status` — update order status (body: `{ "status": "CONFIRMED" }`)
- `POST /api/admin/products` / `PUT /api/admin/products/{id}` / `DELETE /api/admin/products/{id}`
- `POST /api/admin/categories` / `PUT /api/admin/categories/{id}` / `DELETE /api/admin/categories/{id}`

## What's deliberately left out of v1

- No delivery/address handling — pickup only, as decided
- No online payment — order confirmation happens over a phone call
- No product detail pages — just the listing cards
- No customer accounts/login — anyone can place an order with just name + phone
- No stock-blocking on the cart — out-of-stock items are just labeled; unavailable items get mentioned during the confirmation call

## Natural next steps (Phase 2, whenever you're ready)

- Shop info page: hours, address, embedded Google Map (there's already a `Shop` entity/table ready for this — just needs a `GET /api/shop` endpoint and a section on the homepage)
- A proper "change admin password" flow instead of editing the DB by hand
- Minimum order value, or disabling out-of-stock items in the cart, if you decide you want them later
- Deploying it live (a small VPS, Railway, or Render all work fine for a Spring Boot + MySQL app)

## Database credentials

Once you send me your actual MySQL host/username/password, I'll drop them into
`application.properties` for you — just paste them here or tell me and I'll update the file directly.
