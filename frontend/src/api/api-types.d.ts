interface ApiNewTask {
  title: string
  content: string
  deadline: string
  budget: number
  roomId: string
}

interface ApiTask {
  id: string
  title: string
  content: string
  deadline: string
  budget: number
  status: 'New' | 'Assigned' | 'Reviewing' | 'Closed'
  author: string
  authorMemberId: string
  assignee: string | null
  assigneeMemberId: string | null
}

interface ApiWallet {
  id: string
  coinsAmount: number
  memberId: string
}

interface ApiMember {
  id: string
  name: string
}
