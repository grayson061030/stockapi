package com.demo.stock.entity

import com.demo.stock.config.TagType
import jakarta.persistence.*

@Entity
@Table(name = "tags")
class Tag(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id")
    val id: Long? = null,

    @Column(nullable = false, unique = true)
    @Enumerated(EnumType.STRING)
    val name: TagType,

    @Column(nullable = false)
    val description: String
)