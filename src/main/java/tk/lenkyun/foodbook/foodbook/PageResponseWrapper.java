package tk.lenkyun.foodbook.foodbook;

import java.net.URI;

/**
 * Created by lenkyun on 17/11/2558.
 */
public class PageResponseWrapper<E> extends ResponseWrapper<E> {
    public Paging getPaging() {
        return paging;
    }

    public void setPaging(Paging paging) {
        this.paging = paging;
    }

    public static class Paging {
        private URI next = null;
        private URI previous = null;

        public URI getNext() {
            return next;
        }

        public void setNext(URI next) {
            this.next = next;
        }

        public URI getPrevious() {
            return previous;
        }

        public void setPrevious(URI previous) {
            this.previous = previous;
        }
    }

    private Paging paging = new Paging();
}
