trait Turret:
  var isShooting: Boolean
  def position: (Int, Int)
  def hp: Int
  def fireRate: Int

object Turret:
  def apply(x: Int, y: Int): Turret = new BasicTurret(x, y)
  
  private class BasicTurret(x: Int, y: Int) extends Turret:
    var isShooting = false
    override def position: (Int, Int) = (x,y)
    override def hp: Int = 100
    override def fireRate: Int = 1

  



