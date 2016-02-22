/*
                    ,i::,
               :;;;;;;;
              ;:,,::;.
            1ft1;::;1tL
              t1;::;1,
               :;::;               _____       __  ___              __
          fCLff ;:: tfLLC         / ___/      /  |/  /____ _ _____ / /_
         CLft11 :,, i1tffLi       \__ \ ____ / /|_/ // __ `// ___// __ \
         1t1i   .;;   .1tf       ___/ //___// /  / // /_/ // /__ / / / /
       CLt1i    :,:    .1tfL.   /____/     /_/  /_/ \__,_/ \___//_/ /_/
       Lft1,:;:       , 1tfL:
       ;it1i ,,,:::;;;::1tti      s_mach.validate
         .t1i .,::;;; ;1tt        Copyright (c) 2015 S-Mach, Inc.
         Lft11ii;::;ii1tfL:       Author: lance.gatlin@gmail.com
          .L1 1tt1ttt,,Li
            ...1LLLL...
*/
package s_mach.validate

/* WARNING: Generated code. To modify see s_mach.validate.validate.codegen.TupleValidatorImplicitsCodeGen */
trait TupleValidatorImplicits {

  implicit def mkValidator_Tuple2[A,B](implicit
    va: Validator[A],
    vb: Validator[B]
  ) : Validator[(A,B)] =
    Validator.forProductType[(A,B)]


  implicit def mkValidator_Tuple3[A,B,C](implicit
    va: Validator[A],
    vb: Validator[B],
    vc: Validator[C]
  ) : Validator[(A,B,C)] =
    Validator.forProductType[(A,B,C)]


  implicit def mkValidator_Tuple4[A,B,C,D](implicit
    va: Validator[A],
    vb: Validator[B],
    vc: Validator[C],
    vd: Validator[D]
  ) : Validator[(A,B,C,D)] =
    Validator.forProductType[(A,B,C,D)]


  implicit def mkValidator_Tuple5[A,B,C,D,E](implicit
    va: Validator[A],
    vb: Validator[B],
    vc: Validator[C],
    vd: Validator[D],
    ve: Validator[E]
  ) : Validator[(A,B,C,D,E)] =
    Validator.forProductType[(A,B,C,D,E)]


  implicit def mkValidator_Tuple6[A,B,C,D,E,F](implicit
    va: Validator[A],
    vb: Validator[B],
    vc: Validator[C],
    vd: Validator[D],
    ve: Validator[E],
    vf: Validator[F]
  ) : Validator[(A,B,C,D,E,F)] =
    Validator.forProductType[(A,B,C,D,E,F)]


  implicit def mkValidator_Tuple7[A,B,C,D,E,F,G](implicit
    va: Validator[A],
    vb: Validator[B],
    vc: Validator[C],
    vd: Validator[D],
    ve: Validator[E],
    vf: Validator[F],
    vg: Validator[G]
  ) : Validator[(A,B,C,D,E,F,G)] =
    Validator.forProductType[(A,B,C,D,E,F,G)]


  implicit def mkValidator_Tuple8[A,B,C,D,E,F,G,H](implicit
    va: Validator[A],
    vb: Validator[B],
    vc: Validator[C],
    vd: Validator[D],
    ve: Validator[E],
    vf: Validator[F],
    vg: Validator[G],
    vh: Validator[H]
  ) : Validator[(A,B,C,D,E,F,G,H)] =
    Validator.forProductType[(A,B,C,D,E,F,G,H)]


  implicit def mkValidator_Tuple9[A,B,C,D,E,F,G,H,I](implicit
    va: Validator[A],
    vb: Validator[B],
    vc: Validator[C],
    vd: Validator[D],
    ve: Validator[E],
    vf: Validator[F],
    vg: Validator[G],
    vh: Validator[H],
    vi: Validator[I]
  ) : Validator[(A,B,C,D,E,F,G,H,I)] =
    Validator.forProductType[(A,B,C,D,E,F,G,H,I)]


  implicit def mkValidator_Tuple10[A,B,C,D,E,F,G,H,I,J](implicit
    va: Validator[A],
    vb: Validator[B],
    vc: Validator[C],
    vd: Validator[D],
    ve: Validator[E],
    vf: Validator[F],
    vg: Validator[G],
    vh: Validator[H],
    vi: Validator[I],
    vj: Validator[J]
  ) : Validator[(A,B,C,D,E,F,G,H,I,J)] =
    Validator.forProductType[(A,B,C,D,E,F,G,H,I,J)]


  implicit def mkValidator_Tuple11[A,B,C,D,E,F,G,H,I,J,K](implicit
    va: Validator[A],
    vb: Validator[B],
    vc: Validator[C],
    vd: Validator[D],
    ve: Validator[E],
    vf: Validator[F],
    vg: Validator[G],
    vh: Validator[H],
    vi: Validator[I],
    vj: Validator[J],
    vk: Validator[K]
  ) : Validator[(A,B,C,D,E,F,G,H,I,J,K)] =
    Validator.forProductType[(A,B,C,D,E,F,G,H,I,J,K)]


  implicit def mkValidator_Tuple12[A,B,C,D,E,F,G,H,I,J,K,L](implicit
    va: Validator[A],
    vb: Validator[B],
    vc: Validator[C],
    vd: Validator[D],
    ve: Validator[E],
    vf: Validator[F],
    vg: Validator[G],
    vh: Validator[H],
    vi: Validator[I],
    vj: Validator[J],
    vk: Validator[K],
    vl: Validator[L]
  ) : Validator[(A,B,C,D,E,F,G,H,I,J,K,L)] =
    Validator.forProductType[(A,B,C,D,E,F,G,H,I,J,K,L)]


  implicit def mkValidator_Tuple13[A,B,C,D,E,F,G,H,I,J,K,L,M](implicit
    va: Validator[A],
    vb: Validator[B],
    vc: Validator[C],
    vd: Validator[D],
    ve: Validator[E],
    vf: Validator[F],
    vg: Validator[G],
    vh: Validator[H],
    vi: Validator[I],
    vj: Validator[J],
    vk: Validator[K],
    vl: Validator[L],
    vm: Validator[M]
  ) : Validator[(A,B,C,D,E,F,G,H,I,J,K,L,M)] =
    Validator.forProductType[(A,B,C,D,E,F,G,H,I,J,K,L,M)]


  implicit def mkValidator_Tuple14[A,B,C,D,E,F,G,H,I,J,K,L,M,N](implicit
    va: Validator[A],
    vb: Validator[B],
    vc: Validator[C],
    vd: Validator[D],
    ve: Validator[E],
    vf: Validator[F],
    vg: Validator[G],
    vh: Validator[H],
    vi: Validator[I],
    vj: Validator[J],
    vk: Validator[K],
    vl: Validator[L],
    vm: Validator[M],
    vn: Validator[N]
  ) : Validator[(A,B,C,D,E,F,G,H,I,J,K,L,M,N)] =
    Validator.forProductType[(A,B,C,D,E,F,G,H,I,J,K,L,M,N)]


  implicit def mkValidator_Tuple15[A,B,C,D,E,F,G,H,I,J,K,L,M,N,O](implicit
    va: Validator[A],
    vb: Validator[B],
    vc: Validator[C],
    vd: Validator[D],
    ve: Validator[E],
    vf: Validator[F],
    vg: Validator[G],
    vh: Validator[H],
    vi: Validator[I],
    vj: Validator[J],
    vk: Validator[K],
    vl: Validator[L],
    vm: Validator[M],
    vn: Validator[N],
    vo: Validator[O]
  ) : Validator[(A,B,C,D,E,F,G,H,I,J,K,L,M,N,O)] =
    Validator.forProductType[(A,B,C,D,E,F,G,H,I,J,K,L,M,N,O)]


  implicit def mkValidator_Tuple16[A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P](implicit
    va: Validator[A],
    vb: Validator[B],
    vc: Validator[C],
    vd: Validator[D],
    ve: Validator[E],
    vf: Validator[F],
    vg: Validator[G],
    vh: Validator[H],
    vi: Validator[I],
    vj: Validator[J],
    vk: Validator[K],
    vl: Validator[L],
    vm: Validator[M],
    vn: Validator[N],
    vo: Validator[O],
    vp: Validator[P]
  ) : Validator[(A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P)] =
    Validator.forProductType[(A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P)]


  implicit def mkValidator_Tuple17[A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q](implicit
    va: Validator[A],
    vb: Validator[B],
    vc: Validator[C],
    vd: Validator[D],
    ve: Validator[E],
    vf: Validator[F],
    vg: Validator[G],
    vh: Validator[H],
    vi: Validator[I],
    vj: Validator[J],
    vk: Validator[K],
    vl: Validator[L],
    vm: Validator[M],
    vn: Validator[N],
    vo: Validator[O],
    vp: Validator[P],
    vq: Validator[Q]
  ) : Validator[(A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q)] =
    Validator.forProductType[(A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q)]


  implicit def mkValidator_Tuple18[A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R](implicit
    va: Validator[A],
    vb: Validator[B],
    vc: Validator[C],
    vd: Validator[D],
    ve: Validator[E],
    vf: Validator[F],
    vg: Validator[G],
    vh: Validator[H],
    vi: Validator[I],
    vj: Validator[J],
    vk: Validator[K],
    vl: Validator[L],
    vm: Validator[M],
    vn: Validator[N],
    vo: Validator[O],
    vp: Validator[P],
    vq: Validator[Q],
    vr: Validator[R]
  ) : Validator[(A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R)] =
    Validator.forProductType[(A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R)]


  implicit def mkValidator_Tuple19[A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S](implicit
    va: Validator[A],
    vb: Validator[B],
    vc: Validator[C],
    vd: Validator[D],
    ve: Validator[E],
    vf: Validator[F],
    vg: Validator[G],
    vh: Validator[H],
    vi: Validator[I],
    vj: Validator[J],
    vk: Validator[K],
    vl: Validator[L],
    vm: Validator[M],
    vn: Validator[N],
    vo: Validator[O],
    vp: Validator[P],
    vq: Validator[Q],
    vr: Validator[R],
    vs: Validator[S]
  ) : Validator[(A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S)] =
    Validator.forProductType[(A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S)]


  implicit def mkValidator_Tuple20[A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T](implicit
    va: Validator[A],
    vb: Validator[B],
    vc: Validator[C],
    vd: Validator[D],
    ve: Validator[E],
    vf: Validator[F],
    vg: Validator[G],
    vh: Validator[H],
    vi: Validator[I],
    vj: Validator[J],
    vk: Validator[K],
    vl: Validator[L],
    vm: Validator[M],
    vn: Validator[N],
    vo: Validator[O],
    vp: Validator[P],
    vq: Validator[Q],
    vr: Validator[R],
    vs: Validator[S],
    vt: Validator[T]
  ) : Validator[(A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T)] =
    Validator.forProductType[(A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T)]


  implicit def mkValidator_Tuple21[A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U](implicit
    va: Validator[A],
    vb: Validator[B],
    vc: Validator[C],
    vd: Validator[D],
    ve: Validator[E],
    vf: Validator[F],
    vg: Validator[G],
    vh: Validator[H],
    vi: Validator[I],
    vj: Validator[J],
    vk: Validator[K],
    vl: Validator[L],
    vm: Validator[M],
    vn: Validator[N],
    vo: Validator[O],
    vp: Validator[P],
    vq: Validator[Q],
    vr: Validator[R],
    vs: Validator[S],
    vt: Validator[T],
    vu: Validator[U]
  ) : Validator[(A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U)] =
    Validator.forProductType[(A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U)]


  implicit def mkValidator_Tuple22[A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V](implicit
    va: Validator[A],
    vb: Validator[B],
    vc: Validator[C],
    vd: Validator[D],
    ve: Validator[E],
    vf: Validator[F],
    vg: Validator[G],
    vh: Validator[H],
    vi: Validator[I],
    vj: Validator[J],
    vk: Validator[K],
    vl: Validator[L],
    vm: Validator[M],
    vn: Validator[N],
    vo: Validator[O],
    vp: Validator[P],
    vq: Validator[Q],
    vr: Validator[R],
    vs: Validator[S],
    vt: Validator[T],
    vu: Validator[U],
    vv: Validator[V]
  ) : Validator[(A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V)] =
    Validator.forProductType[(A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V)]

}

