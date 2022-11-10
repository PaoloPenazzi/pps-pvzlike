package view.scalagdx

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture

/**
 * Some common mechanisms, useful while working with libGDX.
 */
object Utils:
  /**
   *
   * @param path to the asset to use to generate a texture.
   * @return a [[Texture]] loaded by libGDX from the provided asset.
   */
  def texture(path: String): Texture = new Texture(Gdx.files.classpath(path))

  /**
   * Memoize a function from A to B using a map.
   *
   * @param f a function that computes a value B from a key A.
   * @tparam A the key type.
   * @tparam B the value type.
   * @return a memoized version of f.
   */
  def memoized[A, B](f: A => B): A => B =
    val cache = collection.mutable.Map.empty[A, B]

    a =>
      cache.getOrElse(a, {
        cache.update(a, f(a))
        cache(a)
      })

