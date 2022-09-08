trait Turret:
   def position: (Int, Int)
   def life: Int

object Turret:
  def apply(x: Int, y: Int): Turret = new BasicTurret(x, y)
  
  private class BasicTurret(x: Int, y: Int) extends Turret:
    override def position: (Int, Int) = (x,y)
    override def life: Int = 100

  



