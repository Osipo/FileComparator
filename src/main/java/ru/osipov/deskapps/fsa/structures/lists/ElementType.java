package ru.osipov.deskapps.fsa.structures.lists;

public class ElementType<T> {
    private T element;
    private ElementType<T> next;
    public T getElement(){
        return element;
    }
    public void setElement(T elem) {
        this.element = elem;
    }

    public void setNext(ElementType<T> next) {
        this.next = next;
    }

    public ElementType<T> getNext() {
        return next;
    }
}
