package org.fedon.dfs;

/**
 * Hello world!
 *
 */
public class Main 
{
    public static void main( String[] args )
    {
        System.out.println("Hello Philosophers!");

        Philosopher socrates = new Philosopher("Socrates");
        Philosopher phedon = new Philosopher("Phedon", socrates);
        Philosopher aristoteles = new Philosopher("Aristoteles", phedon);
        Philosopher platones = new Philosopher("Platones", aristoteles);
        Philosopher russel = new Philosopher("Russel", platones, socrates);

        socrates.start();
        phedon.start();
        aristoteles.start();
        platones.start();
        russel.start();
    }
}
