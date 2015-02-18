/**
 * Juego
 *
 * En este juego, chimpy tiene que atacar a los fantasmitas, y por cada uno que 
 * le de, le dara un punto a su score.
 * 
 * Cada 5 juanitos le quitaran una vida al juego, y termina cuando ya no tiene
 * vidas
 *
 * @author Eduardo Jesus Serna Leal a01196007
 * @version 1.0
 * @date 11/02/2015
 */
 
import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.URL;
import java.util.LinkedList;
import javax.swing.*;
import javax.swing.event.*;


/**
 *
 * @author AntonioM
 */
public class Juego extends JFrame implements Runnable, KeyListener{

    private final int iMAXANCHO = 10; // maximo numero de personajes por ancho
    private final int iMAXALTO = 8;  // maxuimo numero de personajes por alto
    private Base basPrincipal;         // Objeto principal
    private Base basMalo;         // Objeto malo
    
    /* objetos para manejar el buffer del Applet y este no parpadee */
    private Image    imaImagenApplet;   // Imagen a proyectar en Applet	
    private Graphics graGraficaApplet;  // Objeto grafico de la Imagen
    private AudioClip adcSonidoChimpy, adcMonkey1;   // Objeto sonido de Chimpy
    //A continuacion se declaran las variables del juego
    private int iNumVidas,iScore, iDireccion,iNumMalos,iVelJuan,iVelFantasmas;
    private int iNumVidasIniciales,iContColisionJuanes;
    private Base basGameOver;
    private LinkedList<Base> lklMalos; //lista de malos Juanitos
    private LinkedList<Base> lklFantasmitas; //lista de malos Fantasmitas
    private URL urlImagenMalo;
    private URL urlImagenFantasma;
    private double dNum; //dNum solo lo uso para posicionar  
    //diferente a todos los juanitos y fantasmas
    private boolean bPausa;
        
    /** 
     * init
     * 
     * Metodo sobrescrito de la clase <code>Applet</code>.<P>
     * En este metodo se inizializan las variables o se crean los objetos
     * a usarse en el <code>Applet</code> y se definen funcionalidades.
     * 
     */
    public void init() {
        // hago el applet de un tamaño 500,500
        setSize(800,500);
             
	URL urlImagenPrincipal = this.getClass().getResource("chimpy.gif");
        
        //Num Aleatorio de Vidas
        iNumVidas = 3 + (int) (Math.random() * (3));
        iNumVidasIniciales=iNumVidas;
        //Se inicializa el score en 0
        iScore=0;
        
        //Se inicializa direccion en 0, chimpy no se mueve
        iDireccion=0;
        iVelJuan=1;
        iVelFantasmas=0;
        iContColisionJuanes=0;
        bPausa=false;
        // se crea el objeto para principal 
	basPrincipal = new Base(0, 0, getWidth() / iMAXANCHO,
                getHeight() / iMAXALTO,
                Toolkit.getDefaultToolkit().getImage(urlImagenPrincipal));

        // se posiciona a principal  en la esquina superior izquierda del Applet 
        basPrincipal.setX(getWidth() / 2);
        basPrincipal.setY(getHeight() / 2);
        
        // defino la imagen del malo
	urlImagenMalo = this.getClass().getResource("juanito.gif");
        
        
        urlImagenFantasma = this.getClass().getResource("fantasmita.gif");
        
        // se crea el objeto para malo 
        int iPosX = (iMAXANCHO - 1) * getWidth() / iMAXANCHO;
        int iPosY = (iMAXALTO - 1) * getHeight() / iMAXALTO;   
        
        lklMalos = new LinkedList();
        lklFantasmitas = new LinkedList();
        
        //Los juanitos se agregan aqui
        iNumMalos = 10 + (int) (Math.random() * (6));
        
        dNum=0.1;
        
        //Agrego a lklMalos los Juanitos
        for (int iI = 0; iI < iNumMalos; iI ++) {  
            basMalo = new Base(0,0, getWidth() / iMAXANCHO,
                getHeight() / iMAXALTO,
                Toolkit.getDefaultToolkit().getImage(urlImagenMalo));
            
            //Los posiciono aleatoriamente en X
            basMalo.setX( (int) (Math.random() * (getWidth() - 
                basMalo.getAncho())) );
            //Los desplazo verticalmente
            basMalo.setY( (int) (-1*dNum * (getHeight() - basMalo.getAlto() ) ) );
            lklMalos.add(basMalo);
            
            dNum+=0.1;           
        }
        
         //Los juanitos se agregan aqui
        iNumMalos = 8 + (int) (Math.random() * (3));
        
        dNum=0.1;
        //Agrego a lklFantasmitas los fantasmitas
        for (int iI = 0; iI < iNumMalos; iI ++) {  
            basMalo = new Base(0,0, getWidth() / iMAXANCHO,
                getHeight() / iMAXALTO,
                Toolkit.getDefaultToolkit().getImage(urlImagenFantasma));
            
            //Los posiciono aleatoriamente en Y
            basMalo.setY( (int) (Math.random() * (getHeight() - 
                basMalo.getAlto())) );
            //Los depslazo horizontalmente
            basMalo.setX( (int) (-1*dNum * (getWidth() - basMalo.getAncho() ) ) );
            lklFantasmitas.add(basMalo);
            
            dNum+=0.1;           
        }
       
        URL urlImagenGameOver = this.getClass().getResource("gameover.gif");
        basGameOver = new Base(0,0, getWidth(),
                getHeight(),
                Toolkit.getDefaultToolkit().getImage(urlImagenGameOver));
                
        
        URL urlSonidoChimpy = Juego.class.getClass().getResource("monkey2.wav");
        URL urlSonidoChimpy1 = Juego.class.getClass().getResource("monkey1.wav");
        adcSonidoChimpy = Applet.newAudioClip(urlSonidoChimpy);
        adcMonkey1 = Applet.newAudioClip(urlSonidoChimpy1);
        adcSonidoChimpy.play();
        
        addKeyListener(this);
    }
	
    /** 
     * start
     * 
     * Metodo sobrescrito de la clase <code>Applet</code>.<P>
     * En este metodo se crea e inicializa el hilo
     * para la animacion este metodo es llamado despues del init o 
     * cuando el usuario visita otra pagina y luego regresa a la pagina
     * en donde esta este <code>Applet</code>
     * 
     */
    public void start () {
        // Declaras un hilo
        Thread th = new Thread (this);
        // Empieza el hilo
        th.start ();
    }
	
    /** 
     * run
     * 
     * Metodo sobrescrito de la clase <code>Thread</code>.<P>
     * En este metodo se ejecuta el hilo, que contendrá las instrucciones
     * de nuestro juego.
     * 
     */
    @Override
    public void run () {
        /* mientras dure el juego, se actualizan posiciones de jugadores
           se checa si hubo colisiones para desaparecer jugadores o corregir
           movimientos y se vuelve a pintar todo
        */ 
        //Se correra el juego mientras num vidas sea mayor a 0
        while (iNumVidas>0) {
            //Si se pausa, se freezea el juego
            if (!bPausa){
                actualiza();
                checaColision();
                repaint();
            }
            
            try	{
                // El thread se duerme.
                Thread.sleep (20);
            }
            catch (InterruptedException iexError) {
                System.out.println("Hubo un error en el juego " + 
                        iexError.toString());
            }
	}
        //Si numvidas es 0, termina el juego
        if(iNumVidas==0){
                System.out.println("Perdiste");
                
        }
    }
	
    /** 
     * actualiza
     * 
     * Metodo que actualiza la posicion de los objetos 
     * 
     */
    public void actualiza(){
        
        //A continuacion checo cuantos juanes han colisionado con la chimpy
        if (iContColisionJuanes==5){
            iNumVidas--;
            iContColisionJuanes=0;
        }
        
        
        //Si las vidas iniciales son 5, la velocidad sera diferente en cada caso
        if (iNumVidasIniciales==5)
        {
            if (iNumVidas==4){
                iVelJuan=2;

            }

            else if (iNumVidas==3){
                iVelJuan=3;

            }

            else if (iNumVidas==2){
                iVelJuan=4;

            }

            else if (iNumVidas==1){
                iVelJuan=5;

            }
            
        }
        
        //Si las vidas iniciales son 4, la velocidad sera diferente en cada caso
        else if (iNumVidasIniciales==4)
        {
           

            if (iNumVidas==3){
                iVelJuan=2;

            }

            else if (iNumVidas==2){
                iVelJuan=3;

            }

            else if (iNumVidas==1){
                iVelJuan=4;

            }
            
        }
        
        //Si las vidas iniciales son 3, la velocidad sera diferente en cada caso
        else if (iNumVidasIniciales==3)
        {
           

            if (iNumVidas==2){
                iVelJuan=2;

            }

            else if (iNumVidas==1){
                iVelJuan=3;

            }

            
        }
        
        
        
        
        switch(iDireccion) {
            case 1: { //se mueve hacia arriba
                basPrincipal.setY(basPrincipal.getY() - 5);
                break;    
            }
            case 2: { //se mueve hacia abajo
                basPrincipal.setY(basPrincipal.getY() + 5);
                break;    
            }
            case 3: { //se mueve hacia izquierda
                basPrincipal.setX(basPrincipal.getX() - 5);
                break;    
            }
            case 4: { //se mueve hacia derecha
                basPrincipal.setX(basPrincipal.getX() + 5);
                break;    	
            }
        }
        //La velocidad de juan dependera de cuantas vidas se tenga
        for (Base basJuanitos : lklMalos) {
            
            basJuanitos.setY(basJuanitos.getY()+iVelJuan);
            
            
        }
        
        for (Base basFantasmas : lklFantasmitas) {
            //La velocidad de los fantamas varia de acuerdo al random,d e 3 a 5
            int iRandom= 3 + (int) (Math.random() * (3));
            basFantasmas.setX(basFantasmas.getX() + iRandom);
            
            
        }

    }
	
    /**
     * checaColision
     * 
     * Metodo usado para checar la colision entre objetos
     * 
     */
    public void checaColision(){
        
        switch(iDireccion){
            case 1: { // si se mueve hacia arriba 
                if(basPrincipal.getY() < 0) { // y esta pasando el limite
                    iDireccion = 0;     
                    
                }
                break;    	
            }     
            case 2: { // si se mueve hacia abajo
                // y se esta saliendo del applet
                if(basPrincipal.getY() + basPrincipal.getAlto() > getHeight()) {
                    iDireccion = 0;     
                 
                }
                break;    	
            } 
            case 3: { // si se mueve hacia izquierda 
                if(basPrincipal.getX() < 0) { // y se sale del applet
                    iDireccion = 0;      
                 
                }
                break;    	
            }    
            case 4: { // si se mueve hacia derecha 
                // si se esta saliendo del applet
                if(basPrincipal.getX() + basPrincipal.getAncho() > getWidth()) { 
                    iDireccion = 0;     
       
                }
                break;    	
            }			
        }
        
        //Checo si los Juanes tocan la parte inferior del applet
        dNum=0.1;
        for (Base basJuanes : lklMalos) {
        

            if (basJuanes.getY()+basJuanes.getAlto()>=getHeight())
            {
       
            //Se reposicionan los Juanes
            basJuanes.setX( (int) (Math.random() * (getWidth() - 
                basJuanes.getAncho())) );
            basJuanes.setY( (int) (-1*dNum * (getHeight() - basJuanes.getAlto() ) ) );
            
            dNum+=0.1;
            }
            
        }
        dNum=0.1;
        //Checo si los Fantasmitas tocan la parte derecha del applet
        for (Base basFantasmitas : lklFantasmitas) {
        
            
            if (basFantasmitas.getX()+basFantasmitas.getAncho()>=getWidth())
            {
       
           //Los posiciono aleatoriamente en Y
            basFantasmitas.setY( (int) (Math.random() * (getHeight() - 
                basMalo.getAlto())) );
            //Los depslazo horizontalmente
            basFantasmitas.setX( (int) (-1*dNum * (getWidth() - basMalo.getAncho() ) ) );
            
            dNum+=0.1;
            }
            
        }
        
        //A continuacion se hace el intersecta de los fantasmitas con chimpy
        dNum=0.1;
        for (Base basFantasmitas : lklFantasmitas) {
            if (basPrincipal.intersecta(basFantasmitas)) {
            
            //Le aumento 1 al score
            iScore++;
            //corre monkey2.wav
            adcSonidoChimpy.play();
            
            //Los posiciono aleatoriamente en Y
            basFantasmitas.setY( (int) (Math.random() * (getHeight() - 
                basMalo.getAlto())) );
            //Los depslazo horizontalmente
            basFantasmitas.setX( (int) (-1*dNum * (getWidth() - basMalo.getAncho() ) ) );
            
            dNum+=0.1;
            
            
            
  
        }
        }
        //Se verifica la colision con los juanes  
        dNum=0.1;
        for (Base basJuanes : lklMalos) {
            if (basPrincipal.intersecta(basJuanes)) {
            
            adcMonkey1.play();
            iContColisionJuanes++;
            //corre monkey2.wav
            adcSonidoChimpy.play();
            
            //Los posiciono aleatoriamente en Y
            basJuanes.setY( (int) (Math.random() * (getHeight() - 
                basMalo.getAlto())) );
            //Los depslazo horizontalmente
            basJuanes.setX( (int) (-1*dNum * (getWidth() - basMalo.getAncho() ) ) );
            
            dNum+=0.1;
            
            
            
  
        }
        }

            }
       
	
    /**
     * update
     * 
     * Metodo sobrescrito de la clase <code>Applet</code>,
     * heredado de la clase Container.<P>
     * En este metodo lo que hace es actualizar el contenedor y 
     * define cuando usar ahora el paint
     * 
     * @param graGrafico es el <code>objeto grafico</code> usado para dibujar.
     * 
     */
    @Override
    public void update (Graphics graGrafico){
        // Inicializan el DoubleBuffer
        if (imaImagenApplet == null){
                imaImagenApplet = createImage (this.getSize().width, 
                        this.getSize().height);
                graGraficaApplet = imaImagenApplet.getGraphics ();
        }

        // Actualiza la imagen de fondo.
        URL urlImagenFondo = this.getClass().getResource("Ciudad.png");
      
       
        Image imaImagenFondo = Toolkit.getDefaultToolkit().getImage(urlImagenFondo);
        graGraficaApplet.drawImage(imaImagenFondo, 0, 0, getWidth(), getHeight(), this);
        
       
        
        // Actualiza el Foreground.
        graGraficaApplet.setColor (getForeground());
        paint(graGraficaApplet);

        // Dibuja la imagen actualizada
        graGrafico.drawImage (imaImagenApplet, 0, 0, this);
    }
    
    /**
     * paint
     * 
     * Metodo sobrescrito de la clase <code>Applet</code>,
     * heredado de la clase Container.<P>
     * En este metodo se dibuja la imagen con la posicion actualizada,
     * ademas que cuando la imagen es cargada te despliega una advertencia.
     * 
     * @param graDibujo es el objeto de <code>Graphics</code> usado para dibujar.
     * 
     */
    @Override
    public void paint(Graphics graDibujo) {
        // si la imagen ya se cargo
        if (basPrincipal != null && lklFantasmitas != null && lklMalos != null) {
               //Si numvidas es 0, se termina el juego
                if (iNumVidas==0){
                    basGameOver.paint(graDibujo, this);
                }
               
               else{
                   
                   //Dibuja la imagen de principal en el Applet
                basPrincipal.paint(graDibujo, this);
                //Dibuja la imagen de malo en el Applet
                
                
                for (Base basFantasmita : lklFantasmitas) {
                    //Dibuja la imagen de fantasmitas en el Applet
                    basFantasmita.paint(graDibujo, this);
                }
                
                for (Base basJuanito : lklMalos) {
                    //Dibuja la imagen de juanito en el Applet
                    basJuanito.paint(graDibujo, this);
                }
                
                
                graDibujo.setColor(Color.red);
                graDibujo.drawString("Vidas "+String.valueOf(iNumVidas), 20, 20);
                graDibujo.drawString("Score "+String.valueOf(iScore), 20, 40);
                   
               }
                
                
        } // sino se ha cargado se dibuja un mensaje 
        else {
                //Da un mensaje mientras se carga el dibujo	
                graDibujo.drawString("No se cargo la imagen..", 20, 20);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        
        
        //Presiono flecha W
        if(keyEvent.getKeyCode() == KeyEvent.VK_W) {    
                iDireccion = 1;  // cambio la dirección arriba
   
           
        }
        // si presiono flecha S
        else if(keyEvent.getKeyCode() == KeyEvent.VK_S) {    
                iDireccion = 2;   // cambio la direccion para abajo
                
        }
        // si presiono flecha A
        else if(keyEvent.getKeyCode() == KeyEvent.VK_A) {    
                iDireccion = 3;   // cambio la direccion a la izquierda
              
        }
        // si presiono flecha D
        else if(keyEvent.getKeyCode() == KeyEvent.VK_D){    
                iDireccion = 4;   // cambio la direccion a la derecha
                
        }
        
        else if(keyEvent.getKeyCode() == KeyEvent.VK_D){    
                iDireccion = 4;   // cambio la direccion a la derecha
                
        }
        
        //Si presiona la tecla escape, es gameover
        else if(keyEvent.getKeyCode() == KeyEvent.VK_ESCAPE){    
                iNumVidas=0;   // gameover
                
        }
        
        //Si presiona la tecla P, se pausa y despausa
        else if(keyEvent.getKeyCode() == KeyEvent.VK_P){    
                bPausa=!bPausa;   // pausa y despausa
                
        }
      
    }

    @Override
    public void keyReleased(KeyEvent e) {
        
    }
    public class panel extends JPanel
    {
        public panel()
        {
            //this is where the diplay items go
        }
    }
    public Juego(){
        init();
        setSize(800,500);
        setVisible(true);
    
    }

    public  static void main(String[] args)
    {
        new Juego();
        
    }


}
