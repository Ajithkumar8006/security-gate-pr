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

## CodeQL SARIF baseline

The approved CodeQL SARIF baseline is stored at `reports/java.sarif`. The CodeQL workflow runs when a PR targeting `main` is opened or updated and compares the scan with the baseline from the PR's target branch. It reports baseline, pre-existing, new, and resolved findings in the Actions summary and creates or updates a summary comment on same-repository PRs. New Critical, High, or Medium findings fail the workflow; Low and Informational findings do not. Update the baseline only after reviewing and approving a new SARIF scan.

Fork PRs use a read-only token and therefore do not receive the PR comment; their report remains available in the Actions run summary.