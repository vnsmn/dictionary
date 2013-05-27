package service;

public interface ActionListener<T> {
  void execute(T value);
}
