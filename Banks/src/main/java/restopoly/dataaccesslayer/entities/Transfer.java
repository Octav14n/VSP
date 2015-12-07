package restopoly.dataaccesslayer.entities;

/**
 * Created by Patrick Steinhauer on 07.12.2015.
 */
public class Transfer {

    private String transferid;
    private String from;
    private String to;
    private int amount;
    private String reason;
    private Event event;

    public Transfer() {

    }

    public String getTransferid() {
        return transferid;
    }

    public void setTransferid(String transferid) {
        this.transferid = transferid;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        Transfer transfer = (Transfer) object;

        if (amount != transfer.amount) return false;
        if (!transferid.equals(transfer.transferid)) return false;
        if (!from.equals(transfer.from)) return false;
        if (!to.equals(transfer.to)) return false;
        if (!reason.equals(transfer.reason)) return false;
        return !(event != null ? !event.equals(transfer.event) : transfer.event != null);

    }

    @Override
    public int hashCode() {
        int result = transferid.hashCode();
        result = 31 * result + from.hashCode();
        result = 31 * result + to.hashCode();
        result = 31 * result + amount;
        result = 31 * result + reason.hashCode();
        result = 31 * result + (event != null ? event.hashCode() : 0);
        return result;
    }
}
