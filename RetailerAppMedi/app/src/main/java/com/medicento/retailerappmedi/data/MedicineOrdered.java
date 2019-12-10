package com.medicento.retailerappmedi.data;

public class MedicineOrdered {

    private String name, manufact_name, item_code, reason, id;

    private int quantity, price;

    private boolean isReturned, isAlreadyReturned;

    public boolean isAlreadyReturned() {
        return isAlreadyReturned;
    }

    public void setAlreadyReturned(boolean alreadyReturned) {
        isAlreadyReturned = alreadyReturned;
    }

    public String getId() {
        return id;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getName() {
        return name;
    }

    public String getManufact_name() {
        return manufact_name;
    }

    public String getItem_code() {
        return item_code;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getPrice() {
        return price;
    }

    public boolean isReturned() {
        return isReturned;
    }

    public void setReturned(boolean returned) {
        isReturned = returned;
    }

    private MedicineOrdered(final Builder builder) {
        this.name = builder.name;
        this.manufact_name = builder.manufact_name;
        this.item_code = builder.item_code;
        this.quantity = builder.quantity;
        this.price = builder.price;
        this.id = builder.id;
        this.isReturned = builder.isReturned;
    }

    public static class Builder {

        String name, manufact_name, item_code, id;

        private boolean isReturned;

        int quantity, price;

        public Builder() {
        }

        public Builder setIsReturned(boolean isReturned) {
            this.isReturned = isReturned;
            return this;
        }

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Builder setPrice(int price) {
            this.price = price;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setManufact_name(String manufact_name) {
            this.manufact_name = manufact_name;
            return this;
        }

        public Builder setItem_code(String item_code) {
            this.item_code = item_code;
            return this;
        }

        public Builder setQuantity(int quantity) {
            this.quantity = quantity;
            return this;
        }

        public MedicineOrdered build() {
            return new MedicineOrdered(this);
        }
    }

}
