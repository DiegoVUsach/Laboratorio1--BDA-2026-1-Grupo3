import { defineStore } from 'pinia'

export interface MemberProfile {
  id: number
  name: string
  level: number
  items: string[]
  dkp: number
}

const initialMembers: MemberProfile[] = [
  {
    id: 1,
    name: 'Ayla Nightblade',
    level: 80,
    items: ['Filo Umbrio', 'Capa de Eclipse'],
    dkp: 1240
  },
  {
    id: 2,
    name: 'Rurik Stormforge',
    level: 78,
    items: ['Martillo de Truenos'],
    dkp: 980
  },
  {
    id: 3,
    name: 'Seraphine Dawn',
    level: 76,
    items: ['Baston Astral', 'Anillo Celeste'],
    dkp: 1125
  }
]

export const useMembersStore = defineStore('members', {
  state: () => ({
    members: initialMembers as MemberProfile[]
  }),
  actions: {
    assignItem(memberId: number, itemName: string) {
      const member = this.members.find((entry) => entry.id === memberId)
      if (!member || !itemName) return
      member.items.push(itemName)
    },
    addDkp(memberId: number, delta: number) {
      const member = this.members.find((entry) => entry.id === memberId)
      if (!member) return
      member.dkp += delta
    },
    removeMember(memberId: number) {
      this.members = this.members.filter((entry) => entry.id !== memberId)
    }
  }
})
