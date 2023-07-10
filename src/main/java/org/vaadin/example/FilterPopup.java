package org.vaadin.example;

import com.vaadin.componentfactory.Popup;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.TextField;

public class FilterPopup extends Composite<Div> {
    private Popup popup;
    private Button button;
    private TextField filterValue;

    public FilterPopup(String key) {
        filterValue = new TextField();
        popup = new Popup();
        popup.add(filterValue);
        button = new Button();
        button.setWidthFull();
        String id = "filter_button_" + key;
        button.setId(id);
        popup.setFor(id);
        getContent().add(button, popup);
    }

    @Override
    protected Div initContent() {
        Div comp = new Div();
        comp.setSizeFull();
        return comp;
    }

    public TextField getInputField() {
        return filterValue;
    }
}
