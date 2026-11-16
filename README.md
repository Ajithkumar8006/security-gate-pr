# Stockroom Inventory

A JavaFX desktop inventory manager with a searchable product catalog, stock status filters, inventory summaries, and CSV export.

## Requirements

- JDK 21 or newer
- Maven 3.9 or newer

## Run

```bash
mvn javafx:run
```

## Features

- Add, edit, and delete products with validation for required fields, numeric values, and duplicate SKUs.
- Search by product name, SKU, or category; filter by in-stock, low-stock, or out-of-stock status.
- See live product counts, inventory value, low-stock count, and category count.
- Export the product catalog as a CSV file.

Products are kept in memory for the current session; restarting the app restores the sample catalog.