package eu.lotusgc.mc.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import eu.lotusgc.mc.main.LotusController;

public class Moneysystem implements CommandExecutor{

    /* baseCommand      arg0     arg1     arg2     arg3
        /money          give     <player> <type>   <amount> *
                        remove   <player> <type>   <amount> *
                        set      <player> <type>   <amount> *
                        balance  <player> *
        /pay            <player> <amount>

        type = bank cash
        amount = integer
        player = string (playername)
     */

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        LotusController lc = new LotusController();

        if(command.getName().equals("money")){
            if(args.length == 0) {
                
            }
        }
        
        return true;
    }
}