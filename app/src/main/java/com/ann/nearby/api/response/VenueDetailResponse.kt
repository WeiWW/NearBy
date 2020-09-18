package com.ann.nearby.api.response

import com.squareup.moshi.Json

data class VenueDetailsResponse(
    val meta: Meta,
    val response: Response
) {
    data class Response(
        @field:Json(name = "venue")
        val venue: VenueDetail
    )
}

data class VenueDetail(
    val allowMenuUrlEdit: Boolean,
    val attributes: Attributes,
    val beenHere: BeenHere,
    val bestPhoto: BestPhoto,
    val canonicalUrl: String,
    val categories: List<Category>,
    val colors: Colors,
    val contact: Contact,
    val createdAt: Int,
    val defaultHours: DefaultHours,
    val description: String,
    val dislike: Boolean,
    val hasMenu: Boolean,
    val hereNow: HereNow,
    val hours: Hours,
    val id: String,
    val inbox: Inbox,
    val likes: Likes,
    val listed: Listed,
    val location: Location,
    val menu: Menu,
    val name: String,
    val ok: Boolean,
    val page: Page,
    val pageUpdates: PageUpdates,
    val photos: Photos,
    val popular: Popular,
    val price: Price,
    val rating: Double,
    val ratingColor: String,
    val ratingSignals: Int,
    val reasons: Reasons,
    val seasonalHours: List<Any>,
    val shortUrl: String,
    val specials: Specials,
    val stats: Stats,
    val timeZone: String,
    val tips: Tips,
    val url: String,
    val venuePage: VenuePage,
    val verified: Boolean
) {
    data class Attributes(
        val groups: List<Group>
    ) {
        data class Group(
            val count: Int,
            val items: List<Item>,
            val name: String,
            val summary: String,
            val type: String
        ) {
            data class Item(
                val displayName: String,
                val displayValue: String,
                val priceTier: Int
            )
        }
    }

    data class BeenHere(
        val count: Int,
        val lastCheckinExpiredAt: Int,
        val marked: Boolean,
        val unconfirmedCount: Int
    )

    data class BestPhoto(
        val createdAt: Int,
        val height: Int,
        val id: String,
        val prefix: String,
        val source: Source,
        val suffix: String,
        val visibility: String,
        val width: Int
    ) {
        data class Source(
            val name: String,
            val url: String
        )
    }

    data class Category(
        val icon: Icon,
        val id: String,
        val name: String,
        val pluralName: String,
        val primary: Boolean,
        val shortName: String
    ) {
        data class Icon(
            val prefix: String,
            val suffix: String
        )
    }

    data class Colors(
        val algoVersion: Int,
        val highlightColor: HighlightColor,
        val highlightTextColor: HighlightTextColor
    ) {
        data class HighlightColor(
            val photoId: String,
            val value: Int
        )

        data class HighlightTextColor(
            val photoId: String,
            val value: Int
        )
    }

    data class Contact(
        val facebook: String,
        val facebookName: String,
        val formattedPhone: String,
        val instagram: String,
        val phone: String
    )

    data class DefaultHours(
        val dayData: List<Any>,
        val isLocalHoliday: Boolean,
        val isOpen: Boolean,
        val richStatus: RichStatus,
        val status: String,
        val timeframes: List<Timeframe>
    ) {
        data class RichStatus(
            val entities: List<Any>,
            val text: String
        )

        data class Timeframe(
            val days: String,
            val includesToday: Boolean,
            val `open`: List<Open>,
            val segments: List<Any>
        ) {
            data class Open(
                val renderedTime: String
            )
        }
    }

    data class HereNow(
        val count: Int,
        val groups: List<Group>,
        val summary: String
    ) {
        data class Group(
            val count: Int,
            val items: List<Any>,
            val name: String,
            val type: String
        )
    }

    data class Hours(
        val dayData: List<Any>,
        val isLocalHoliday: Boolean,
        val isOpen: Boolean,
        val richStatus: RichStatus,
        val status: String,
        val timeframes: List<Timeframe>
    ) {
        data class RichStatus(
            val entities: List<Any>,
            val text: String
        )

        data class Timeframe(
            val days: String,
            val includesToday: Boolean,
            val `open`: List<Open>,
            val segments: List<Any>
        ) {
            data class Open(
                val renderedTime: String
            )
        }
    }

    data class Inbox(
        val count: Int,
        val items: List<Any>
    )

    data class Likes(
        val count: Int,
        val groups: List<Group>,
        val summary: String
    ) {
        data class Group(
            val count: Int,
            val items: List<Any>,
            val type: String
        )
    }

    data class Listed(
        val count: Int,
        val groups: List<Group>
    ) {
        data class Group(
            val count: Int,
            val items: List<Item>,
            val name: String,
            val type: String
        ) {
            data class Item(
                val canonicalUrl: String,
                val collaborative: Boolean,
                val createdAt: Int,
                val description: String,
                val editable: Boolean,
                val followers: Followers,
                val guide: Boolean,
                val guideType: String,
                val id: String,
                val listItems: ListItems,
                val logView: Boolean,
                val name: String,
                val photo: Photo,
                val `public`: Boolean,
                val readMoreUrl: String,
                val type: String,
                val updatedAt: Int,
                val url: String,
                val user: User
            ) {
                data class Followers(
                    val count: Int
                )

                data class ListItems(
                    val count: Int,
                    val items: List<Item>
                ) {
                    data class Item(
                        val createdAt: Int,
                        val id: String,
                        val photo: Photo
                    ) {
                        data class Photo(
                            val createdAt: Int,
                            val height: Int,
                            val id: String,
                            val prefix: String,
                            val suffix: String,
                            val user: User,
                            val visibility: String,
                            val width: Int
                        ) {
                            data class User(
                                val firstName: String,
                                val id: String,
                                val lastName: String,
                                val photo: Photo,
                                val type: String
                            ) {
                                data class Photo(
                                    val prefix: String,
                                    val suffix: String
                                )
                            }
                        }
                    }
                }

                data class Photo(
                    val createdAt: Int,
                    val height: Int,
                    val id: String,
                    val prefix: String,
                    val suffix: String,
                    val user: User,
                    val visibility: String,
                    val width: Int
                ) {
                    data class User(
                        val firstName: String,
                        val id: String,
                        val photo: Photo,
                        val type: String
                    ) {
                        data class Photo(
                            val prefix: String,
                            val suffix: String
                        )
                    }
                }

                data class User(
                    val firstName: String,
                    val id: String,
                    val photo: Photo,
                    val type: String
                ) {
                    data class Photo(
                        val prefix: String,
                        val suffix: String
                    )
                }
            }
        }
    }

    data class Location(
        val address: String,
        val cc: String,
        val city: String,
        val country: String,
        val crossStreet: String,
        val formattedAddress: List<String>,
        val labeledLatLngs: List<LabeledLatLng>,
        val lat: Double,
        val lng: Double,
        val neighborhood: String,
        val postalCode: String,
        val state: String
    ) {
        data class LabeledLatLng(
            val label: String,
            val lat: Double,
            val lng: Double
        )
    }

    data class Menu(
        val anchor: String,
        val label: String,
        val mobileUrl: String,
        val type: String,
        val url: String
    )

    data class Page(
        val user: User
    ) {
        data class User(
            val bio: String,
            val firstName: String,
            val id: String,
            val lists: Lists,
            val photo: Photo,
            val tips: Tips,
            val type: String,
            val venue: Venue
        ) {
            data class Lists(
                val groups: List<Group>
            ) {
                data class Group(
                    val count: Int,
                    val items: List<Any>,
                    val type: String
                )
            }

            data class Photo(
                val prefix: String,
                val suffix: String
            )

            data class Tips(
                val count: Int
            )

            data class Venue(
                val id: String
            )
        }
    }

    data class PageUpdates(
        val count: Int,
        val items: List<Any>
    )

    data class Photos(
        val count: Int,
        val groups: List<Group>
    ) {
        data class Group(
            val count: Int,
            val items: List<Item>,
            val name: String,
            val type: String
        ) {
            data class Item(
                val createdAt: Int,
                val height: Int,
                val id: String,
                val prefix: String,
                val source: Source,
                val suffix: String,
                val user: User,
                val visibility: String,
                val width: Int
            ) {
                data class Source(
                    val name: String,
                    val url: String
                )

                data class User(
                    val firstName: String,
                    val id: String,
                    val photo: Photo,
                    val type: String
                ) {
                    data class Photo(
                        val prefix: String,
                        val suffix: String
                    )
                }
            }
        }
    }

    data class Popular(
        val isLocalHoliday: Boolean,
        val isOpen: Boolean,
        val richStatus: RichStatus,
        val status: String,
        val timeframes: List<Timeframe>
    ) {
        data class RichStatus(
            val entities: List<Any>,
            val text: String
        )

        data class Timeframe(
            val days: String,
            val includesToday: Boolean,
            val `open`: List<Open>,
            val segments: List<Any>
        ) {
            data class Open(
                val renderedTime: String
            )
        }
    }

    data class Price(
        val currency: String,
        val message: String,
        val tier: Int
    )

    data class Reasons(
        val count: Int,
        val items: List<Item>
    ) {
        data class Item(
            val reasonName: String,
            val summary: String,
            val type: String
        )
    }

    data class Specials(
        val count: Int,
        val items: List<Any>
    )

    data class Stats(
        val tipCount: Int
    )

    data class Tips(
        val count: Int,
        val groups: List<Group>
    ) {
        data class Group(
            val count: Int,
            val items: List<Item>,
            val name: String,
            val type: String
        ) {
            data class Item(
                val agreeCount: Int,
                val canonicalUrl: String,
                val createdAt: Int,
                val disagreeCount: Int,
                val id: String,
                val lang: String,
                val likes: Likes,
                val logView: Boolean,
                val text: String,
                val todo: Todo,
                val type: String,
                val user: User
            ) {
                data class Likes(
                    val count: Int,
                    val groups: List<Group>,
                    val summary: String
                ) {
                    data class Group(
                        val count: Int,
                        val items: List<Any>,
                        val type: String
                    )
                }

                data class Todo(
                    val count: Int
                )

                data class User(
                    val firstName: String,
                    val id: String,
                    val photo: Photo,
                    val type: String
                ) {
                    data class Photo(
                        val prefix: String,
                        val suffix: String
                    )
                }
            }
        }
    }

    data class VenuePage(
        val id: String
    )
}