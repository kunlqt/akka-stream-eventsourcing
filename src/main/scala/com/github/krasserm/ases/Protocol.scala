/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.krasserm.ases

import java.util.UUID

/**
  * Event delivery protocol implemented by event logs and sources.
  *
  *  - Emit 0-n replayed events as `Delivered` message.
  *  - Emit `Recovered` to signal recovery completion.
  *  - Emit 0-n live events as `Delivered` message.
  */
sealed trait Delivery[+A]

/**
  * Emitted by an event log or source to signal recovery completion
  * (= all events have been replayed).
  */
case object Recovered extends Delivery[Nothing]

/**
  * Emitted by an event log or source to deliver an event (replayed
  * or live).
  */
case class Delivered[A](a: A) extends Delivery[A]

/**
  * Metadata and container of an event emitted by an `EventSourcing` stage.
  *
  * @param event Emitted event.
  * @param emitterId Id of the event emitter.
  * @param emissionUuid Emission id generated by the event emitter.
  * @tparam A Event type
  */
case class Emitted[+A](event: A, emitterId: String, emissionUuid: String = UUID.randomUUID().toString) {
  def durable(sequenceNr: Long): Durable[A] = Durable(event, emitterId, emissionUuid, sequenceNr)
}

/**
  * Metadata and container of a durable event emitted by an event log or source.
  *
  * @param event Durable event.
  * @param emitterId Id of the initial event emitter.
  * @param emissionUuid Emission id generated by the initial event emitter.
  * @param sequenceNr Sequence number of the durable event.
  * @tparam A Event type
  */
case class Durable[+A](event: A, emitterId: String, emissionUuid: String, sequenceNr: Long)
