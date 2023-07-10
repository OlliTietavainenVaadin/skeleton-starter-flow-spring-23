package org.vaadin.example;

import java.util.*;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;

@Route(value = "table")
public class TableView extends Main implements HasUrlParameter<Integer> {
    private MenuBar menu;
    private MenuItem activateFilter;
    private MenuItem deactivateFilter;
    private List<TableData> data;
    private Grid<TableData> grid;
    private Map<String, TextField> filterValues;
    private Map<Object, Registration> listeners = new HashMap<>();

    public TableView() {
        menu = new MenuBar();
        deactivateFilter = menu.addItem("Clear Filter");
        listeners.put(deactivateFilter, deactivateFilter.addClickListener(e -> {
            filterValues.values().forEach(TextField::clear);
            update(null);
        }));
        setSizeFull();
    }

    private void createData(int size) {
        data = new Random(42 + size).ints(size).collect(LinkedList::new, (list, i) -> list.add(new TableData(i)), List::addAll);
    }

    private void createGrid() {
        grid = new Grid<>();
        grid.setItems(data);
        for (String k : TableData.Generator.KEYS) {
            Grid.Column<TableData> col = grid.addColumn(td -> td.get(k));
            col.setHeader(k);
            col.setKey(k);
            col.setWidth("8em");
        }
        createFilterHeaderWithTextbox();
    }

    private Component createLayout() {
        VerticalLayout layout = new VerticalLayout(menu, grid);
        layout.setSizeFull();
        return layout;
    }

    private void createFilterHeaderWithPopup() {
        Optional.ofNullable(filterValues).ifPresent(
                map -> map.values().forEach(
                        tf -> Optional.ofNullable(listeners.get(tf)).ifPresent(Registration::remove)));
        filterValues = new HashMap<>(TableData.SIZE);
        if (grid.getHeaderRows().size() < 2) {
            grid.appendHeaderRow();
        }
        HeaderRow filterRow = grid.getHeaderRows().get(1);
        for (int i = 0; i < filterRow.getCells().size(); i++) {
            String key = grid.getColumns().get(i).getKey();
            FilterPopup popup = new FilterPopup(key);
            filterValues.put(key, popup.getInputField());
            filterRow.getCells().get(i).setComponent(popup);
            listeners.put(popup.getInputField(), popup.getInputField().addValueChangeListener(this::update));
        }
        update(null);
    }

    private void createFilterHeaderWithTextbox() {
        Optional.ofNullable(filterValues).ifPresent(
                map -> map.values().forEach(
                        tf -> Optional.ofNullable(listeners.get(tf)).ifPresent(Registration::remove)));
        filterValues = new HashMap<>(TableData.SIZE);
        if (grid.getHeaderRows().size() < 2) {
            grid.appendHeaderRow();
        }
        HeaderRow filterRow = grid.getHeaderRows().get(1);
        for (int i = 0; i < filterRow.getCells().size(); i++) {
            String key = grid.getColumns().get(i).getKey();
            TextField textFieldFilterValue = new TextField();
            filterValues.put(key, textFieldFilterValue);
            filterRow.getCells().get(i).setComponent(textFieldFilterValue);
            listeners.put(textFieldFilterValue, textFieldFilterValue.addValueChangeListener(this::update));
        }
        update(null);
    }

    private void update(AbstractField.ComponentValueChangeEvent<TextField, String> changeEvent) {
        List<TableData> current = new LinkedList<>(data);
        filterValues.forEach((k, v) -> {
            if (!v.isEmpty()) {
                current.removeIf(d -> Optional.ofNullable(d.get(k)).map(x -> !x.contains(v.getValue())).orElse(false));
            }
        });
        grid.setItems(current);
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, @OptionalParameter Integer integer) {
        createData(Optional.ofNullable(integer).orElse(100));
        createGrid();

        add(createLayout());
    }
}
