package com.stockroom;

import java.time.LocalDate;
import java.util.Locale;
import java.util.Optional;

import javafx.application.Application;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ButtonBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class InventoryApplication extends Application {
    private static final String[] CATEGORIES = {"Accessories", "Audio", "Furniture", "Lighting", "Storage"};
    private final ObservableList<Product> products = FXCollections.observableArrayList(
            new Product("ST-1042", "Studio Headphones", "Audio", 24, 89.00, 8),
            new Product("ST-1038", "Arc Desk Lamp", "Lighting", 7, 64.50, 10),
            new Product("ST-1031", "Oak Monitor Stand", "Furniture", 18, 48.00, 6),
            new Product("ST-1027", "Cable Organizer Set", "Accessories", 3, 16.00, 8),
            new Product("ST-1022", "Felt Desk Mat", "Accessories", 42, 32.00, 12),
            new Product("ST-1019", "Modular Shelf Unit", "Storage", 0, 129.00, 5),
            new Product("ST-1014", "Wireless Keyboard", "Accessories", 15, 74.00, 6),
            new Product("ST-1008", "Pendant Light", "Lighting", 11, 112.00, 4));

    private final FilteredList<Product> filteredProducts = new FilteredList<>(products, product -> true);
    private Label totalProductsValue;
    private Label inventoryValue;
    private Label lowStockValue;
    private Label categoryValue;
    private Label showingProductsValue;
    private TableView<Product> table;
    private TextField searchField;
    private ComboBox<String> statusFilter;

    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("app-root");
        root.setLeft(createSidebar());
        root.setCenter(createMainContent());

        Scene scene = new Scene(root, 1280, 820);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        stage.setTitle("Stockroom | Inventory");
        stage.setMinWidth(980);
        stage.setMinHeight(680);
        stage.setScene(scene);
        stage.show();
        updateMetrics();
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox(26);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPadding(new Insets(28, 16, 20, 16));
        sidebar.setPrefWidth(226);

        HBox brand = new HBox(11, new Label("S"), new Label("stockroom"));
        brand.getStyleClass().add("brand");
        brand.getChildren().get(0).getStyleClass().add("brand-mark");
        brand.getChildren().get(1).getStyleClass().add("brand-name");

        Label workspaceLabel = new Label("WORKSPACE");
        workspaceLabel.getStyleClass().add("nav-caption");
        Button overview = navButton("▦", "Overview", false);
        Button inventory = navButton("▤", "Inventory", true);
        Button orders = navButton("⇄", "Orders", false);
        Button suppliers = navButton("◇", "Suppliers", false);
        for (Button button : new Button[]{overview, orders, suppliers}) {
            button.setOnAction(event -> showComingSoon(button.getText().substring(2).trim()));
        }

        Label manageLabel = new Label("MANAGE");
        manageLabel.getStyleClass().add("nav-caption");
        Button reports = navButton("▥", "Reports", false);
        reports.setOnAction(event -> showComingSoon("Reports"));

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        HBox user = new HBox(11, new Label("JD"), new VBox(3, new Label("Jamie Davis"), new Label("Store manager")));
        user.setAlignment(Pos.CENTER_LEFT);
        user.getStyleClass().add("user-profile");
        user.getChildren().get(0).getStyleClass().add("avatar");
        ((VBox) user.getChildren().get(1)).getChildren().get(0).getStyleClass().add("user-name");
        ((VBox) user.getChildren().get(1)).getChildren().get(1).getStyleClass().add("user-role");

        sidebar.getChildren().addAll(brand, workspaceLabel, overview, inventory, orders, suppliers,
                new Separator(), manageLabel, reports, spacer, user);
        return sidebar;
    }

    private Button navButton(String icon, String text, boolean active) {
        Button button = new Button(icon + "   " + text);
        button.getStyleClass().add("nav-button");
        if (active) {
            button.getStyleClass().add("nav-button-active");
        }
        button.setMaxWidth(Double.MAX_VALUE);
        button.setAlignment(Pos.CENTER_LEFT);
        return button;
    }

    private ScrollPane createMainContent() {
        VBox content = new VBox(24);
        content.getStyleClass().add("main-content");
        content.setPadding(new Insets(34, 40, 36, 40));

        HBox topbar = new HBox(12);
        topbar.setAlignment(Pos.CENTER_RIGHT);
        Label date = new Label(LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("EEEE, MMMM d", Locale.ENGLISH)));
        date.getStyleClass().add("date-label");
        Button helpButton = new Button("? ");
        helpButton.getStyleClass().add("icon-button");
        helpButton.setOnAction(event -> new Alert(Alert.AlertType.INFORMATION, "Your inventory data is stored for this session.").showAndWait());
        topbar.getChildren().addAll(date, helpButton);

        VBox heading = new VBox(7, new Label("Inventory"), new Label("Keep your products, stock, and value in one place."));
        heading.getChildren().get(0).getStyleClass().add("page-title");
        heading.getChildren().get(1).getStyleClass().add("page-subtitle");

        HBox metrics = new HBox(14,
                metricCard("TOTAL PRODUCTS", "0", "Unique items in catalog", "metric-blue", "↗"),
                metricCard("INVENTORY VALUE", "$0", "At current unit prices", "metric-green", "$"),
                metricCard("LOW STOCK", "0", "Items at or below reorder point", "metric-amber", "!"),
                metricCard("CATEGORIES", "0", "Across your catalog", "metric-violet", "◇"));
        HBox.setHgrow(metrics, Priority.ALWAYS);
        for (var node : metrics.getChildren()) {
            HBox.setHgrow(node, Priority.ALWAYS);
        }

        HBox sectionHeading = new HBox(12);
        sectionHeading.setAlignment(Pos.CENTER_LEFT);
        VBox sectionTitles = new VBox(5, new Label("All products"), new Label("Track and manage your current stock."));
        sectionTitles.getChildren().get(0).getStyleClass().add("section-title");
        sectionTitles.getChildren().get(1).getStyleClass().add("section-subtitle");
        Region sectionSpacer = new Region();
        HBox.setHgrow(sectionSpacer, Priority.ALWAYS);
        Button exportButton = new Button("↓   Export");
        exportButton.getStyleClass().add("secondary-button");
        exportButton.setOnAction(event -> exportProducts());
        Button addButton = new Button("＋   Add product");
        addButton.getStyleClass().add("primary-button");
        addButton.setOnAction(event -> openProductDialog(null));
        sectionHeading.getChildren().addAll(sectionTitles, sectionSpacer, exportButton, addButton);

        HBox filters = new HBox(10);
        filters.setAlignment(Pos.CENTER_LEFT);
        searchField = new TextField();
        searchField.setPromptText("Search name, SKU, or category...");
        searchField.getStyleClass().add("search-field");
        searchField.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        HBox.setHgrow(searchField, Priority.ALWAYS);
        statusFilter = new ComboBox<>(FXCollections.observableArrayList("All stock", "In stock", "Low stock", "Out of stock"));
        statusFilter.setValue("All stock");
        statusFilter.getStyleClass().add("filter-combo");
        statusFilter.setOnAction(event -> applyFilters());
        filters.getChildren().addAll(searchField, statusFilter);

        table = createTable();
        VBox tableFrame = new VBox(table);
        tableFrame.getStyleClass().add("table-frame");
        VBox.setVgrow(tableFrame, Priority.ALWAYS);

        showingProductsValue = new Label();
        HBox footer = new HBox(showingProductsValue, new Region(), new Label("Stockroom · Main warehouse"));
        footer.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(footer.getChildren().get(1), Priority.ALWAYS);
        footer.getStyleClass().add("table-footer");

        content.getChildren().addAll(topbar, heading, metrics, sectionHeading, filters, tableFrame, footer);
        VBox.setVgrow(tableFrame, Priority.ALWAYS);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("content-scroll");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        return scrollPane;
    }

    private HBox metricCard(String title, String value, String caption, String tone, String iconText) {
        Label icon = new Label(iconText);
        icon.getStyleClass().addAll("metric-icon", tone);
        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().add("metric-value");
        Label captionLabel = new Label(caption);
        captionLabel.getStyleClass().add("metric-caption");
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("metric-title");
        VBox text = new VBox(7, titleLabel, valueLabel, captionLabel);
        HBox card = new HBox(12, text, icon);
        card.setAlignment(Pos.TOP_LEFT);
        HBox.setHgrow(text, Priority.ALWAYS);
        card.getStyleClass().add("metric-card");
        if (title.equals("TOTAL PRODUCTS")) totalProductsValue = valueLabel;
        if (title.equals("INVENTORY VALUE")) inventoryValue = valueLabel;
        if (title.equals("LOW STOCK")) lowStockValue = valueLabel;
        if (title.equals("CATEGORIES")) categoryValue = valueLabel;
        return card;
    }

    private TableView<Product> createTable() {
        TableView<Product> productTable = new TableView<>(filteredProducts);
        productTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        productTable.setPlaceholder(new Label("No products match your filters."));
        productTable.getStyleClass().add("product-table");

        TableColumn<Product, String> productColumn = new TableColumn<>("PRODUCT");
        productColumn.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().name));
        productColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String name, boolean empty) {
                super.updateItem(name, empty);
                if (empty || name == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    Product product = getTableView().getItems().get(getIndex());
                    Label initials = new Label(product.initials());
                    initials.getStyleClass().add("product-initials");
                    VBox details = new VBox(4, new Label(name), new Label(product.sku));
                    details.getStyleClass().add("product-cell-text");
                    details.getChildren().get(0).getStyleClass().add("product-name");
                    details.getChildren().get(1).getStyleClass().add("product-sku");
                    HBox cell = new HBox(11, initials, details);
                    cell.setAlignment(Pos.CENTER_LEFT);
                    setGraphic(cell);
                    setText(null);
                }
            }
        });
        productColumn.setPrefWidth(250);

        TableColumn<Product, String> categoryColumn = new TableColumn<>("CATEGORY");
        categoryColumn.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().category));

        TableColumn<Product, String> stockColumn = new TableColumn<>("STOCK");
        stockColumn.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().quantity + " units"));
        stockColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String value, boolean empty) {
                super.updateItem(value, empty);
                if (empty || value == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Product product = getTableView().getItems().get(getIndex());
                    Label badge = new Label(value);
                    badge.getStyleClass().add("stock-badge");
                    if (product.quantity == 0) badge.getStyleClass().add("stock-out");
                    else if (product.quantity <= product.reorderAt) badge.getStyleClass().add("stock-low");
                    else badge.getStyleClass().add("stock-good");
                    setGraphic(badge);
                    setText(null);
                }
            }
        });

        TableColumn<Product, String> priceColumn = new TableColumn<>("UNIT PRICE");
        priceColumn.setCellValueFactory(cell -> new ReadOnlyStringWrapper(currency(cell.getValue().price)));

        TableColumn<Product, String> valueColumn = new TableColumn<>("TOTAL VALUE");
        valueColumn.setCellValueFactory(cell -> new ReadOnlyStringWrapper(currency(cell.getValue().price * cell.getValue().quantity)));

        TableColumn<Product, Void> actionsColumn = new TableColumn<>("");
        actionsColumn.setSortable(false);
        actionsColumn.setPrefWidth(92);
        actionsColumn.setCellFactory(column -> new TableCell<>() {
            private final Button edit = new Button("Edit");
            private final Button delete = new Button("×");
            private final HBox actions = new HBox(5, edit, delete);
            {
                edit.getStyleClass().add("row-action");
                delete.getStyleClass().add("row-delete");
                delete.setTooltip(new javafx.scene.control.Tooltip("Delete product"));
                edit.setOnAction(event -> openProductDialog(getTableView().getItems().get(getIndex())));
                delete.setOnAction(event -> confirmDelete(getTableView().getItems().get(getIndex())));
                actions.setAlignment(Pos.CENTER_RIGHT);
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : actions);
            }
        });

        productTable.getColumns().add(productColumn);
        productTable.getColumns().add(categoryColumn);
        productTable.getColumns().add(stockColumn);
        productTable.getColumns().add(priceColumn);
        productTable.getColumns().add(valueColumn);
        productTable.getColumns().add(actionsColumn);
        return productTable;
    }

    private void applyFilters() {
        String query = searchField.getText() == null ? "" : searchField.getText().trim().toLowerCase(Locale.ROOT);
        String selectedStatus = statusFilter.getValue();
        filteredProducts.setPredicate(product -> {
            boolean matchesQuery = query.isEmpty()
                    || product.name.toLowerCase(Locale.ROOT).contains(query)
                    || product.sku.toLowerCase(Locale.ROOT).contains(query)
                    || product.category.toLowerCase(Locale.ROOT).contains(query);
            boolean matchesStatus = switch (selectedStatus) {
                case "In stock" -> product.quantity > product.reorderAt;
                case "Low stock" -> product.quantity > 0 && product.quantity <= product.reorderAt;
                case "Out of stock" -> product.quantity == 0;
                default -> true;
            };
            return matchesQuery && matchesStatus;
        });
        updateFooter();
    }

    private void openProductDialog(Product existing) {
        Dialog<Product> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "Add product" : "Edit product");
        dialog.setHeaderText(existing == null ? "Add an item to your catalog" : "Update product details");

        TextField nameField = new TextField(existing == null ? "" : existing.name);
        nameField.setPromptText("e.g. Studio Headphones");
        TextField skuField = new TextField(existing == null ? "" : existing.sku);
        skuField.setPromptText("e.g. ST-1042");
        ComboBox<String> categoryField = new ComboBox<>(FXCollections.observableArrayList(CATEGORIES));
        categoryField.setEditable(true);
        if (existing != null) categoryField.setValue(existing.category);
        else categoryField.setValue(CATEGORIES[0]);
        TextField quantityField = new TextField(existing == null ? "" : Integer.toString(existing.quantity));
        quantityField.setPromptText("0");
        TextField priceField = new TextField(existing == null ? "" : Double.toString(existing.price));
        priceField.setPromptText("0.00");
        TextField reorderField = new TextField(existing == null ? "5" : Integer.toString(existing.reorderAt));

        VBox form = new VBox(8,
                fieldLabel("Product name", nameField),
                fieldLabel("SKU", skuField),
                fieldLabel("Category", categoryField),
                fieldLabel("Quantity", quantityField),
                fieldLabel("Unit price ($)", priceField),
                fieldLabel("Reorder point", reorderField));
        form.setPadding(new Insets(10, 0, 0, 0));
        dialog.getDialogPane().setContent(form);
        ButtonType saveType = new ButtonType(existing == null ? "Add product" : "Save changes", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveType, ButtonType.CANCEL);
        dialog.getDialogPane().lookupButton(saveType).addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            try {
                validateProduct(nameField, skuField, categoryField, quantityField, priceField, reorderField, existing);
            } catch (IllegalArgumentException exception) {
                event.consume();
                new Alert(Alert.AlertType.WARNING, exception.getMessage(), ButtonType.OK).showAndWait();
            }
        });
        dialog.setResultConverter(button -> {
            if (button != saveType) return null;
            return new Product(skuField.getText().trim(), nameField.getText().trim(), categoryField.getValue().trim(),
                    Integer.parseInt(quantityField.getText().trim()), Double.parseDouble(priceField.getText().trim()),
                    Integer.parseInt(reorderField.getText().trim()));
        });

        Optional<Product> result = dialog.showAndWait();
        result.ifPresent(product -> {
            if (existing == null) products.add(product);
            else {
                existing.sku = product.sku;
                existing.name = product.name;
                existing.category = product.category;
                existing.quantity = product.quantity;
                existing.price = product.price;
                existing.reorderAt = product.reorderAt;
                table.refresh();
            }
            applyFilters();
            updateMetrics();
        });
    }

    private VBox fieldLabel(String label, javafx.scene.control.Control control) {
        Label title = new Label(label);
        title.getStyleClass().add("form-label");
        control.setMaxWidth(Double.MAX_VALUE);
        return new VBox(5, title, control);
    }

    private void validateProduct(TextField name, TextField sku, ComboBox<String> category, TextField quantity,
                                 TextField price, TextField reorder, Product existing) {
        if (name.getText().isBlank() || sku.getText().isBlank() || category.getValue() == null || category.getValue().isBlank()) {
            throw new IllegalArgumentException("Name, SKU, and category are required.");
        }
        boolean duplicateSku = products.stream().anyMatch(product -> product.sku.equalsIgnoreCase(sku.getText().trim()) && product != existing);
        if (duplicateSku) throw new IllegalArgumentException("That SKU is already in use.");
        try {
            int amount = Integer.parseInt(quantity.getText().trim());
            int reorderPoint = Integer.parseInt(reorder.getText().trim());
            double unitPrice = Double.parseDouble(price.getText().trim());
            if (amount < 0 || reorderPoint < 0 || unitPrice < 0 || !Double.isFinite(unitPrice)) {
                throw new IllegalArgumentException("Quantity, price, and reorder point must be zero or greater.");
            }
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Enter whole numbers for quantity and reorder point, and a valid price.");
        }
    }

    private void confirmDelete(Product product) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION,
                "Remove “" + product.name + "” from your inventory?", ButtonType.CANCEL, ButtonType.OK);
        confirmation.setHeaderText("Delete product");
        confirmation.showAndWait().filter(button -> button == ButtonType.OK).ifPresent(button -> {
            products.remove(product);
            updateMetrics();
            updateFooter();
        });
    }

    private void updateMetrics() {
        int lowStockCount = (int) products.stream().filter(product -> product.quantity <= product.reorderAt).count();
        int categoryCount = (int) products.stream().map(product -> product.category).distinct().count();
        double totalValue = products.stream().mapToDouble(product -> product.quantity * product.price).sum();
        totalProductsValue.setText(Integer.toString(products.size()));
        inventoryValue.setText(currency(totalValue));
        lowStockValue.setText(Integer.toString(lowStockCount));
        categoryValue.setText(Integer.toString(categoryCount));
        updateFooter();
    }

    private void updateFooter() {
        int visibleCount = filteredProducts.size();
        showingProductsValue.setText("Showing " + visibleCount + (visibleCount == 1 ? " product" : " products"));
    }

    private void exportProducts() {
        javafx.stage.FileChooser chooser = new javafx.stage.FileChooser();
        chooser.setTitle("Export inventory");
        chooser.setInitialFileName("stockroom-inventory.csv");
        chooser.getExtensionFilters().add(new javafx.stage.FileChooser.ExtensionFilter("CSV files", "*.csv"));
        java.io.File file = chooser.showSaveDialog(table.getScene().getWindow());
        if (file == null) return;
        try (java.io.PrintWriter writer = new java.io.PrintWriter(file, java.nio.charset.StandardCharsets.UTF_8)) {
            writer.println("SKU,Product,Category,Quantity,Unit Price,Reorder Point");
            for (Product product : products) {
                writer.printf("%s,%s,%s,%d,%.2f,%d%n", csv(product.sku), csv(product.name), csv(product.category),
                        product.quantity, product.price, product.reorderAt);
            }
        } catch (java.io.IOException exception) {
            new Alert(Alert.AlertType.ERROR, "Could not export the inventory: " + exception.getMessage()).showAndWait();
        }
    }

    private String csv(String value) {
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }

    private void showComingSoon(String section) {
        new Alert(Alert.AlertType.INFORMATION, section + " is not available in this demo yet.").showAndWait();
    }

    private String currency(double value) {
        return String.format(Locale.US, "$%,.2f", value);
    }

    private static final class Product {
        private String sku;
        private String name;
        private String category;
        private int quantity;
        private double price;
        private int reorderAt;

        private Product(String sku, String name, String category, int quantity, double price, int reorderAt) {
            this.sku = sku;
            this.name = name;
            this.category = category;
            this.quantity = quantity;
            this.price = price;
            this.reorderAt = reorderAt;
        }

        private String initials() {
            String[] words = name.split("\\s+");
            return words.length == 1 ? words[0].substring(0, 1).toUpperCase(Locale.ROOT)
                    : (words[0].substring(0, 1) + words[1].substring(0, 1)).toUpperCase(Locale.ROOT);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}