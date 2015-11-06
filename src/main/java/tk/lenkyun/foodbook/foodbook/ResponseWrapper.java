package tk.lenkyun.foodbook.foodbook;

/**
 * Created by lenkyun on 6/11/2558.
 */
public class ResponseWrapper<E> {
    private E result;
    private int error;
    private String detail;

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }

    public E getResult() {
        return result;
    }

    public void setResult(E result) {
        this.result = result;
    }
}
