package view
import model.entities.Entity

object View {
  trait EntityRenderer:
    def renderEntities(entities: List[Entity]): Unit
}
