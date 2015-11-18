package restopoly.dataaccesslayer.entities;

import javax.validation.constraints.NotNull;

/**
 * Created by Paddy-Gaming on 14.11.2015.
 */
public class BankAccount {

    private @NotNull GamePlayer player;
    private int saldo;

    public BankAccount(GamePlayer player, int saldo) {
        this.player = player;
        this.saldo = saldo;
    }

    // Needed for Spring.
    private BankAccount() {}

    public GamePlayer getPlayer() {
        return player;
    }

    public int getSaldo() {
        return saldo;
    }

    public void setSaldo(int saldo) {
        this.saldo = saldo;
    }

    public void addSaldo(int saldo) {
        this.saldo += saldo;
    }

    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        BankAccount that = (BankAccount) o;

        if (saldo != that.saldo)
            return false;
        return !(player != null ? !player.equals(that.player) : that.player != null);

    }

    @Override public int hashCode() {
        int result = player != null ? player.hashCode() : 0;
        result = 31 * result + saldo;
        return result;
    }
}
