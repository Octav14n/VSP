package restopoly.banks.dataaccesslayer.entities;

import javax.validation.constraints.NotNull;

/**
 * Created by Paddy-Gaming on 14.11.2015.
 */
public class BankAccount {

    private @NotNull Player player;
    private int saldo;

    public BankAccount(Player player, int saldo) {
        this.player = player;
        this.saldo = saldo;
    }

    // Needed for Spring.
    private BankAccount() {}

    /**
     * This method will get the owner of this bankAccount.
     *
     * @return Returns a player, which is the owner of the bankAccount.
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * This method will get the saldo of the bankAccount.
     *
     * @return Returns the saldo from the bankAccount.
     */
    public int getSaldo() {
        return saldo;
    }

    /**
     * This method will set the saldo for the bankAccount.
     *
     * @param saldo The Saldo which will be set for the bankAccount.
     */
    public void setSaldo(int saldo) {
        this.saldo = saldo;
    }

    /**
     * This method adds a new saldo to the bankAccount.
     *
     * @param saldo The saldo which will be added to the bankAccount.
     */
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
